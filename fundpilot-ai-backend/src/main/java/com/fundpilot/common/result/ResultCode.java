package com.fundpilot.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 通用
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    // 参数校验 4xx
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),

    // 业务错误 1xxx
    FUND_NOT_FOUND(1001, "基金不存在"),
    FUND_CODE_INVALID(1002, "基金代码无效"),
    PORTFOLIO_NOT_FOUND(1003, "组合不存在"),
    PORTFOLIO_LIMIT_EXCEEDED(1004, "组合数量已达上限"),
    ALERT_NOT_FOUND(1005, "预警规则不存在"),
    CHAT_SESSION_EXPIRED(1006, "会话已过期"),

    // AI 相关 2xxx
    AI_SERVICE_ERROR(2001, "AI服务调用失败"),
    AI_TOOL_ERROR(2002, "工具调用异常"),
    AI_TOKEN_LIMIT(2003, "Token额度不足");

    /** 响应码 */
    private final int code;

    /** 响应消息 */
    private final String message;
}
