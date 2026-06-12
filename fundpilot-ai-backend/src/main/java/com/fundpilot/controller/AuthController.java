package com.fundpilot.controller;

import com.fundpilot.common.result.R;
import com.fundpilot.dto.UserLoginDTO;
import com.fundpilot.dto.UserRegisterDTO;
import com.fundpilot.service.UserService;
import com.fundpilot.vo.LoginVO;
import com.fundpilot.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "用户认证", description = "登录注册接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R<UserVO> register(@Valid @RequestBody UserRegisterDTO dto) {
        return R.ok(userService.register(dto));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        return R.ok(userService.login(dto));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public R<UserVO> me() {
        return R.ok(userService.getCurrentUser());
    }
}
