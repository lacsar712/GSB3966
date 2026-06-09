package com.trading.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private Integer status;
    private String statusText;
    private Long roleId;
    private String roleCode;
    private String roleName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
