package com.fundpilot.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应 VO
 */
@Data
@Builder
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Sa-Token 令牌 */
    private String token;

    /** 用户信息 */
    private UserVO user;
}
