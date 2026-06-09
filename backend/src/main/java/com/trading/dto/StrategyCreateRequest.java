package com.trading.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StrategyCreateRequest {
    
    @NotBlank(message = "策略名称不能为空")
    @Size(max = 50, message = "策略名称最多50字符")
    private String strategyName;
    
    @NotBlank(message = "策略类型不能为空")
    private String strategyType;
    
    private String tradingSymbol;
    
    private String description;
    
    private String parameters;
    
    private String riskControl;
}
