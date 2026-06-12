package com.fundpilot.common.constant;

/**
 * 缓存 key 常量
 */
public final class CacheConstant {

    private CacheConstant() {}

    /** 基金信息缓存前缀 */
    public static final String FUND_INFO = "fund:info:";

    /** 基金净值缓存前缀 */
    public static final String FUND_NAV = "fund:nav:";

    /** 基金列表缓存前缀 */
    public static final String FUND_LIST = "fund:list:";

    /** 用户组合缓存前缀 */
    public static final String PORTFOLIO = "portfolio:";

    /** AI 对话会话缓存前缀 */
    public static final String CHAT_SESSION = "chat:session:";

    /** 基金经理缓存前缀 */
    public static final String MANAGER = "manager:";

    /** 缓存过期时间 (秒) */
    public static final long DEFAULT_TTL = 3600L;

    /** 净值缓存过期时间 (秒) - 1天 */
    public static final long NAV_TTL = 86400L;
}
