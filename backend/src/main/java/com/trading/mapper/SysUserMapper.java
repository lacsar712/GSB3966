package com.trading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trading.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface SysUserMapper extends BaseMapper<SysUser> {
    
    @Select("SELECT u.*, r.role_code, r.role_name " +
            "FROM sys_user u " +
            "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN sys_role r ON ur.role_id = r.id " +
            "WHERE u.username = #{username}")
    SysUser selectByUsernameWithRole(@Param("username") String username);
    
    @Select("SELECT u.*, r.role_code, r.role_name " +
            "FROM sys_user u " +
            "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN sys_role r ON ur.role_id = r.id " +
            "WHERE u.id = #{id}")
    SysUser selectByIdWithRole(@Param("id") Long id);
}
