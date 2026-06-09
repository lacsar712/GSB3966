package com.trading.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("trading_strategy")
public class TradingStrategy {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String strategyName;
    
    private String strategyCode;
    
    private String strategyType;
    
    private String tradingSymbol;
    
    private String description;
    
    private String parameters;
    
    private String riskControl;
    
    private Integer status;
    
    private Long creatorId;
    
    private Long reviewerId;
    
    private LocalDateTime reviewTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private String creatorName;
    
    @TableField(exist = false)
    private String reviewerName;
}
