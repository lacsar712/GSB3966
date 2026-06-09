package com.trading.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trading.entity.SysRole;
import com.trading.mapper.SysRoleMapper;
import com.trading.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {
    
    private final SysRoleMapper roleMapper;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<SysRole>> list() {
        List<SysRole> roles = roleMapper.selectList(
                new LambdaQueryWrapper<SysRole>().orderByAsc(SysRole::getId));
        return Result.success(roles);
    }
}
