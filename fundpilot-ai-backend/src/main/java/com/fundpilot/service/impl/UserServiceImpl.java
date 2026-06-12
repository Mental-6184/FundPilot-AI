package com.fundpilot.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fundpilot.common.exception.BizException;
import com.fundpilot.common.result.ResultCode;
import com.fundpilot.dto.UserLoginDTO;
import com.fundpilot.dto.UserRegisterDTO;
import com.fundpilot.entity.User;
import com.fundpilot.mapper.UserMapper;
import com.fundpilot.service.UserService;
import com.fundpilot.vo.LoginVO;
import com.fundpilot.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public UserVO register(UserRegisterDTO dto) {
        // 检查用户名唯一
        User exist = getByUsername(dto.getUsername());
        if (exist != null) {
            throw new BizException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("USER");
        user.setStatus(1);
        save(user);

        log.info("用户注册成功: {}", dto.getUsername());
        return toVO(user);
    }

    @Override
    public LoginVO login(UserLoginDTO dto) {
        User user = getByUsername(dto.getUsername());
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            throw new BizException("账号已被禁用");
        }
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }

        // Sa-Token 登录
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        // 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        updateById(user);

        log.info("用户登录成功: {}", dto.getUsername());
        return LoginVO.builder()
                .token(token)
                .user(toVO(user))
                .build();
    }

    @Override
    public UserVO getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = getById(userId);
        if (user == null) {
            throw new BizException(ResultCode.UNAUTHORIZED);
        }
        return toVO(user);
    }

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public UserVO toVO(User user) {
        if (user == null) return null;
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
