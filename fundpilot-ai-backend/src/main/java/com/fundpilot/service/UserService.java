package com.fundpilot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fundpilot.dto.UserLoginDTO;
import com.fundpilot.dto.UserRegisterDTO;
import com.fundpilot.entity.User;
import com.fundpilot.vo.LoginVO;
import com.fundpilot.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    UserVO register(UserRegisterDTO dto);

    /**
     * 用户登录
     */
    LoginVO login(UserLoginDTO dto);

    /**
     * 获取当前登录用户信息
     */
    UserVO getCurrentUser();

    /**
     * 根据用户名查询
     */
    User getByUsername(String username);

    /**
     * Entity → VO
     */
    UserVO toVO(User user);
}
