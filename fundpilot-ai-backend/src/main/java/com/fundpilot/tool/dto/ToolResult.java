package com.fundpilot.tool.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Tool 统一返回包装
 *
 * @param <T> 数据类型
 */
@Data
public class ToolResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否成功 */
    private boolean success;

    /** 消息 */
    private String message;

    /** 数据 */
    private T data;

    private ToolResult() {}

    public static <T> ToolResult<T> ok(T data) {
        ToolResult<T> r = new ToolResult<>();
        r.success = true;
        r.message = "success";
        r.data = data;
        return r;
    }

    public static <T> ToolResult<T> ok(String message, T data) {
        ToolResult<T> r = new ToolResult<>();
        r.success = true;
        r.message = message;
        r.data = data;
        return r;
    }

    public static <T> ToolResult<T> success(String message, T data) {
        ToolResult<T> r = new ToolResult<>();
        r.success = true;
        r.message = message;
        r.data = data;
        return r;
    }

    public static <T> ToolResult<T> fail(String message) {
        ToolResult<T> r = new ToolResult<>();
        r.success = false;
        r.message = message;
        return r;
    }
}
