package com.trading.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.trading.dto.StrategyCreateRequest;
import com.trading.dto.StrategyReviewRequest;
import com.trading.dto.StrategyUpdateRequest;
import com.trading.entity.TradingStrategy;
import com.trading.vo.PageResult;
import com.trading.vo.StrategyVO;

public interface TradingStrategyService extends IService<TradingStrategy> {
    
    PageResult<StrategyVO> getStrategyList(Integer pageNum, Integer pageSize, String keyword, 
                                           String strategyType, Integer status, Long currentUserId, String roleCode);
    
    StrategyVO getStrategyDetail(Long id);
    
    void createStrategy(StrategyCreateRequest request, Long creatorId);
    
    void updateStrategy(Long id, StrategyUpdateRequest request, Long currentUserId, String roleCode);
    
    void deleteStrategy(Long id, Long currentUserId, String roleCode);
    
    void submitForReview(Long id, Long currentUserId);
    
    void reviewStrategy(Long id, StrategyReviewRequest request, Long reviewerId);
    
    void startStrategy(Long id, Long currentUserId, String roleCode);
    
    void pauseStrategy(Long id, Long currentUserId, String roleCode);
    
    void archiveStrategy(Long id, Long currentUserId, String roleCode);
}
