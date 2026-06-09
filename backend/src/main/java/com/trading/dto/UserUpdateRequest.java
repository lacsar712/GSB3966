package com.trading.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    
    private String realName;
    
    private String email;
    
    private String phone;
    
    private Integer status;
    
    private Long roleId;
}
