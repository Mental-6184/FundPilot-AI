package com.fundpilot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * Spring AI 配置
 *
 * <p>修复 DashScope 返回 application/octet-stream 导致 Spring AI 解析失败的问题。
 * 通过 RestClient.Builder 添加响应拦截器，将 octet-stream 响应的 Content-Type 修正为 application/json。</p>
 */
@Slf4j
@Configuration
public class AiConfig {

    /**
     * 自定义 RestClient.Builder — Spring AI OpenAI 自动配置会使用此 Bean 构建 RestClient。
     * 添加拦截器修复 DashScope 返回 octet-stream 的问题。
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .requestInterceptor(fixDashScopeContentType());
    }

    /**
     * 响应拦截器：将 DashScope 返回的 application/octet-stream 修正为 application/json
     *
     * <p>DashScope 的 OpenAI 兼容模式在某些情况下（Tool Calling 响应、长文本响应等）
     * 会返回 Content-Type: application/octet-stream 而非 application/json，
     * 导致 Spring AI 的 OpenAiApi 反序列化失败。</p>
     */
    private ClientHttpRequestInterceptor fixDashScopeContentType() {
        return (request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request, body);
            String contentType = response.getHeaders().getContentType() != null
                    ? response.getHeaders().getContentType().toString()
                    : "";

            if (contentType.contains("application/octet-stream")) {
                log.debug("[AiConfig] 检测到 DashScope octet-stream 响应，修正为 application/json");
                return new JsonContentTypeResponseWrapper(response);
            }
            return response;
        };
    }

    /**
     * 包装 ClientHttpResponse，将 Content-Type 强制设为 application/json
     */
    private static class JsonContentTypeResponseWrapper implements ClientHttpResponse {

        private final ClientHttpResponse delegate;

        JsonContentTypeResponseWrapper(ClientHttpResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        public HttpHeaders getHeaders() {
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(delegate.getHeaders());
            headers.setContentType(MediaType.APPLICATION_JSON);
            return headers;
        }

        @Override
        public InputStream getBody() throws IOException {
            return delegate.getBody();
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return delegate.getStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return delegate.getStatusText();
        }

        @Override
        public void close() {
            delegate.close();
        }
    }

    /**
     * 默认系统提示词 Bean — Agent 构建 ChatClient 时可引用
     */
    @Bean
    public String defaultSystemPrompt() {
        return """
                你是 FundPilot AI，专业的基金投资分析助手。
                你的职责：
                1. 理解用户的基金投资相关问题
                2. 通过工具获取真实的基金数据（禁止编造数据）
                3. 基于 Java 计算的精确数据给出分析建议
                4. 生成专业、易懂的分析报告

                规则：
                - 所有基金数据必须通过工具获取，禁止凭空编造
                - 数字必须精确，保留合理小数位
                - 分析结论必须基于真实数据
                - 提醒用户投资有风险
                """;
    }
}
