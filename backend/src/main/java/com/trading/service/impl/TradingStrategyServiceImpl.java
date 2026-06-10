package com.trading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trading.dto.StrategyCreateRequest;
import com.trading.dto.StrategyReviewRequest;
import com.trading.dto.StrategyUpdateRequest;
import com.trading.entity.TradingStrategy;
import com.trading.exception.BusinessException;
import com.trading.mapper.TradingStrategyMapper;
import com.trading.security.UserPrincipal;
import com.trading.service.TradingStrategyService;
import com.trading.vo.PageResult;
import com.trading.vo.StrategyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradingStrategyServiceImpl extends ServiceImpl<TradingStrategyMapper, TradingStrategy> 
        implements TradingStrategyService {
    
    private final TradingStrategyMapper strategyMapper;
    
    @Override
    public PageResult<StrategyVO> getStrategyList(Integer pageNum, Integer pageSize, String keyword, 
                                                   String strategyType, Integer status, 
                                                   Long currentUserId, String roleCode) {
        Page<TradingStrategy> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TradingStrategy> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(TradingStrategy::getStrategyName, keyword)
                    .or()
                    .like(TradingStrategy::getStrategyCode, keyword));
        }
        if (StringUtils.hasText(strategyType)) {
            wrapper.eq(TradingStrategy::getStrategyType, strategyType);
        }
        if (status != null) {
            wrapper.eq(TradingStrategy::getStatus, status);
        }
        
        if ("TRADER".equals(roleCode)) {
            wrapper.eq(TradingStrategy::getCreatorId, currentUserId);
        }
        
        wrapper.orderByDesc(TradingStrategy::getCreateTime);
        page = this.page(page, wrapper);
        
        List<StrategyVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageResult.<StrategyVO>builder()
                .list(voList)
                .total(page.getTotal())
                .pageNum(page.getCurrent())
                .pageSize(page.getSize())
                .totalPages(page.getPages())
                .build();
    }
    
    @Override
    public StrategyVO getStrategyDetail(Long id) {
        TradingStrategy strategy = strategyMapper.selectByIdWithCreator(id);
        if (strategy == null) {
            throw new BusinessException("策略不存在");
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal currentUser = (UserPrincipal) authentication.getPrincipal();
            if ("TRADER".equals(currentUser.getRoleCode()) 
                    && !strategy.getCreatorId().equals(currentUser.getUserId())) {
                throw new BusinessException("无权查看此策略");
            }
        }
        
        return convertToVO(strategy);
    }
    
    @Override
    @Transactional
    public void createStrategy(StrategyCreateRequest request, Long creatorId) {
        TradingStrategy strategy = new TradingStrategy();
        BeanUtils.copyProperties(request, strategy);
        strategy.setStrategyCode(generateStrategyCode());
        strategy.setStatus(0);
        strategy.setCreatorId(creatorId);
        this.save(strategy);
    }
    
    @Override
    @Transactional
    public void updateStrategy(Long id, StrategyUpdateRequest request, Long currentUserId, String roleCode) {
        TradingStrategy strategy = this.getById(id);
        if (strategy == null) {
            throw new BusinessException("策略不存在");
        }
        if (strategy.getStatus() != 0) {
            throw new BusinessException("只有草稿状态的策略可以编辑");
        }
        if (!"ADMIN".equals(roleCode) && !strategy.getCreatorId().equals(currentUserId)) {
            throw new BusinessException("无权编辑此策略");
        }
        
        BeanUtils.copyProperties(request, strategy);
        this.updateById(strategy);
    }
    
    @Override
    @Transactional
    public void deleteStrategy(Long id, Long currentUserId, String roleCode) {
        TradingStrategy strategy = this.getById(id);
        if (strategy == null) {
            throw new BusinessException("策略不存在");
        }
        if (!"ADMIN".equals(roleCode) && !strategy.getCreatorId().equals(currentUserId)) {
            throw new BusinessException("无权删除此策略");
        }
        this.removeById(id);
    }
    
    @Override
    @Transactional
    public void submitForReview(Long id, Long currentUserId) {
        TradingStrategy strategy = this.getById(id);
        if (strategy == null) {
            throw new BusinessException("策略不存在");
        }
        if (!strategy.getCreatorId().equals(currentUserId)) {
            throw new BusinessException("无权提交此策略");
        }
        if (strategy.getStatus() != 0) {
            throw new BusinessException("只有草稿状态的策略可以提交审核");
        }
        strategy.setStatus(1);
        this.updateById(strategy);
    }
    
    @Override
    @Transactional
    public void reviewStrategy(Long id, StrategyReviewRequest request, Long reviewerId) {
        TradingStrategy strategy = this.getById(id);
        if (strategy == null) {
            throw new BusinessException("策略不存在");
        }
        if (strategy.getStatus() != 1) {
            throw new BusinessException("只有待审核状态的策略可以进行审核");
        }
        
        strategy.setStatus(request.getApproved() ? 2 : 0);
        strategy.setReviewerId(reviewerId);
        strategy.setReviewTime(LocalDateTime.now());
        this.updateById(strategy);
    }
    
    @Override
    @Transactional
    public void startStrategy(Long id, Long currentUserId, String roleCode) {
        TradingStrategy strategy = this.getById(id);
        if (strategy == null) {
            throw new BusinessException("策略不存在");
        }
        if (strategy.getStatus() != 2 && strategy.getStatus() != 3) {
            throw new BusinessException("只有已发布或已暂停的策略可以启动");
        }
        if (!"ADMIN".equals(roleCode) && !"MANAGER".equals(roleCode)) {
            throw new BusinessException("无权操作此策略");
        }
        strategy.setStatus(2);
        this.updateById(strategy);
    }
    
    @Override
    @Transactional
    public void pauseStrategy(Long id, Long currentUserId, String roleCode) {
        TradingStrategy strategy = this.getById(id);
        if (strategy == null) {
            throw new BusinessException("策略不存在");
        }
        if (strategy.getStatus() != 2) {
            throw new BusinessException("只有已发布的策略可以暂停");
        }
        if (!"ADMIN".equals(roleCode) && !"MANAGER".equals(roleCode)) {
            throw new BusinessException("无权操作此策略");
        }
        strategy.setStatus(3);
        this.updateById(strategy);
    }
    
    @Override
    @Transactional
    public void archiveStrategy(Long id, Long currentUserId, String roleCode) {
        TradingStrategy strategy = this.getById(id);
        if (strategy == null) {
            throw new BusinessException("策略不存在");
        }
        if (!"ADMIN".equals(roleCode) && !"MANAGER".equals(roleCode)) {
            throw new BusinessException("无权归档此策略");
        }
        strategy.setStatus(4);
        this.updateById(strategy);
    }
    
    private String generateStrategyCode() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = this.count(new LambdaQueryWrapper<TradingStrategy>()
                .likeRight(TradingStrategy::getStrategyCode, "STR-" + dateStr));
        return String.format("STR-%s-%03d", dateStr, count + 1);
    }
    
    private StrategyVO convertToVO(TradingStrategy strategy) {
        StrategyVO vo = new StrategyVO();
        BeanUtils.copyProperties(strategy, vo);
        vo.setStatusText(getStatusText(strategy.getStatus()));
        return vo;
    }
    
    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "草稿";
            case 1: return "待审核";
            case 2: return "已发布";
            case 3: return "已暂停";
            case 4: return "已归档";
            default: return "未知";
        }
    }
}
