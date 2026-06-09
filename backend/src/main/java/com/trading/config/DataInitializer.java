package com.trading.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trading.entity.SysRole;
import com.trading.entity.SysUser;
import com.trading.entity.SysUserRole;
import com.trading.mapper.SysRoleMapper;
import com.trading.mapper.SysUserMapper;
import com.trading.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    
    private final SysRoleMapper roleMapper;
    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Initializing system data...");
        
        initRoles();
        initAdminUser();
        initTestUsers();
        
        log.info("System data initialization completed.");
    }
    
    private void initRoles() {
        String[][] roles = {
            {"ADMIN", "系统管理员", "系统管理员，拥有所有权限"},
            {"MANAGER", "策略管理员", "策略审核、启停管理"},
            {"TRADER", "策略交易员", "策略创建、编辑"},
            {"VISITOR", "访客", "只读访问"}
        };
        
        for (String[] role : roles) {
            if (roleMapper.selectCount(
                    new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, role[0])) == 0) {
                SysRole sysRole = new SysRole();
                sysRole.setRoleCode(role[0]);
                sysRole.setRoleName(role[1]);
                sysRole.setDescription(role[2]);
                roleMapper.insert(sysRole);
                log.info("Created role: {}", role[0]);
            }
        }
    }
    
    private void initAdminUser() {
        if (userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "admin")) == 0) {
            SysUser admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRealName("系统管理员");
            admin.setEmail("admin@trading.com");
            admin.setPhone("13800138000");
            admin.setStatus(1);
            userMapper.insert(admin);
            
            SysRole adminRole = roleMapper.selectOne(
                    new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, "ADMIN"));
            if (adminRole != null) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(admin.getId());
                userRole.setRoleId(adminRole.getId());
                userRoleMapper.insert(userRole);
            }
            log.info("Created admin user");
        }
    }
    
    private void initTestUsers() {
        String[][] users = {
            {"manager", "manager123", "策略管理员", "MANAGER"},
            {"trader", "trader123", "交易员", "TRADER"},
            {"trader2", "trader123", "交易员2", "TRADER"}
        };
        
        for (String[] userInfo : users) {
            if (userMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, userInfo[0])) == 0) {
                SysUser user = new SysUser();
                user.setUsername(userInfo[0]);
                user.setPassword(passwordEncoder.encode(userInfo[1]));
                user.setRealName(userInfo[2]);
                user.setEmail(userInfo[0] + "@trading.com");
                user.setStatus(1);
                userMapper.insert(user);
                
                SysRole role = roleMapper.selectOne(
                        new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, userInfo[3]));
                if (role != null) {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(user.getId());
                    userRole.setRoleId(role.getId());
                    userRoleMapper.insert(userRole);
                }
                log.info("Created test user: {}", userInfo[0]);
            }
        }
    }
}
