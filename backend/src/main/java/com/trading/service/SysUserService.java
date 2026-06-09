package com.trading.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.trading.dto.*;
import com.trading.entity.SysUser;
import com.trading.vo.PageResult;
import com.trading.vo.UserVO;

public interface SysUserService extends IService<SysUser> {
    
    LoginResponse login(LoginRequest request);
    
    UserVO getCurrentUser(Long userId);
    
    PageResult<UserVO> getUserList(Integer pageNum, Integer pageSize, String keyword);
    
    void createUser(UserCreateRequest request);
    
    void updateUser(Long id, UserUpdateRequest request);
    
    void deleteUser(Long id);
    
    void changePassword(Long id, PasswordChangeRequest request);
}
