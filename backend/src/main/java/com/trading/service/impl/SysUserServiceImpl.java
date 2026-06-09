package com.trading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trading.dto.*;
import com.trading.entity.SysRole;
import com.trading.entity.SysUser;
import com.trading.entity.SysUserRole;
import com.trading.exception.BusinessException;
import com.trading.mapper.SysRoleMapper;
import com.trading.mapper.SysUserMapper;
import com.trading.mapper.SysUserRoleMapper;
import com.trading.security.JwtTokenProvider;
import com.trading.service.SysUserService;
import com.trading.vo.PageResult;
import com.trading.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        SysUser user = userMapper.selectByUsernameWithRole(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRoleCode());
        
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .roleCode(user.getRoleCode())
                .roleName(user.getRoleName())
                .build();
    }
    
    @Override
    public UserVO getCurrentUser(Long userId) {
        SysUser user = userMapper.selectByIdWithRole(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }
    
    @Override
    public PageResult<UserVO> getUserList(Integer pageNum, Integer pageSize, String keyword) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getRealName, keyword);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        
        page = this.page(page, wrapper);
        
        List<UserVO> voList = page.getRecords().stream()
                .map(user -> {
                    SysUser userWithRole = userMapper.selectByIdWithRole(user.getId());
                    return convertToVO(userWithRole != null ? userWithRole : user);
                })
                .collect(Collectors.toList());
        
        return PageResult.<UserVO>builder()
                .list(voList)
                .total(page.getTotal())
                .pageNum(page.getCurrent())
                .pageSize(page.getSize())
                .totalPages(page.getPages())
                .build();
    }
    
    @Override
    @Transactional
    public void createUser(UserCreateRequest request) {
        if (lambdaQuery().eq(SysUser::getUsername, request.getUsername()).count() > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        this.save(user);
        
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(request.getRoleId());
        userRoleMapper.insert(userRole);
    }
    
    @Override
    @Transactional
    public void updateUser(Long id, UserUpdateRequest request) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        this.updateById(user);
        
        if (request.getRoleId() != null) {
            userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(id);
            userRole.setRoleId(request.getRoleId());
            userRoleMapper.insert(userRole);
        }
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (this.getById(id) == null) {
            throw new BusinessException("用户不存在");
        }
        this.removeById(id);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
    }
    
    @Override
    public void changePassword(Long id, PasswordChangeRequest request) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        this.updateById(user);
    }
    
    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setStatusText(user.getStatus() == 1 ? "启用" : "禁用");
        
        if (user.getRoleCode() != null) {
            SysRole role = roleMapper.selectOne(
                    new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, user.getRoleCode()));
            if (role != null) {
                vo.setRoleId(role.getId());
                vo.setRoleCode(role.getRoleCode());
                vo.setRoleName(role.getRoleName());
            }
        }
        return vo;
    }
}
