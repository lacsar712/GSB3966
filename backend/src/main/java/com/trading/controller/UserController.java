package com.trading.controller;

import com.trading.dto.PasswordChangeRequest;
import com.trading.dto.UserCreateRequest;
import com.trading.dto.UserUpdateRequest;
import com.trading.security.UserPrincipal;
import com.trading.service.SysUserService;
import com.trading.vo.PageResult;
import com.trading.vo.Result;
import com.trading.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final SysUserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<UserVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        PageResult<UserVO> result = userService.getUserList(pageNum, pageSize, keyword);
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    public Result<UserVO> detail(@PathVariable Long id) {
        UserVO vo = userService.getCurrentUser(id);
        return Result.success(vo);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> create(@Valid @RequestBody UserCreateRequest request) {
        userService.createUser(request);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
    
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordChangeRequest request,
                                       @AuthenticationPrincipal UserPrincipal userPrincipal) {
        userService.changePassword(userPrincipal.getUserId(), request);
        return Result.success();
    }
}
