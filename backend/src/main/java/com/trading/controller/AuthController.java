package com.trading.controller;

import com.trading.dto.LoginRequest;
import com.trading.dto.LoginResponse;
import com.trading.security.JwtTokenProvider;
import com.trading.security.UserPrincipal;
import com.trading.service.SysUserService;
import com.trading.vo.Result;
import com.trading.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final SysUserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }
    
    @GetMapping("/info")
    public Result<UserVO> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserVO user = userService.getCurrentUser(userPrincipal.getUserId());
        return Result.success(user);
    }
    
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}
