package com.fundpilot.agent.prompt;

import java.util.Map;

/**
 * Prompt 模板引擎
 * 支持 ${key} 占位符替换
 */
public final class PromptTemplate {

    private PromptTemplate() {}

    /**
     * 渲染模板
     *
     * @param template 模板字符串（含 ${key} 占位符）
     * @param vars     变量映射
     * @return 渲染后的字符串
     */
    public static String render(String template, Map<String, String> vars) {
        String result = template;
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            result = result.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}
