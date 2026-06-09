package com.trading.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StrategyVO {
    
    private Long id;
    private String strategyName;
    private String strategyCode;
    private String strategyType;
    private String tradingSymbol;
    private String description;
    private String parameters;
    private String riskControl;
    private Integer status;
    private String statusText;
    private Long creatorId;
    private String creatorName;
    private Long reviewerId;
    private String reviewerName;
    private LocalDateTime reviewTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
