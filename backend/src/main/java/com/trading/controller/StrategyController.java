package com.trading.controller;

import com.trading.dto.StrategyCreateRequest;
import com.trading.dto.StrategyReviewRequest;
import com.trading.dto.StrategyUpdateRequest;
import com.trading.security.UserPrincipal;
import com.trading.service.TradingStrategyService;
import com.trading.vo.PageResult;
import com.trading.vo.Result;
import com.trading.vo.StrategyVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/strategy")
@RequiredArgsConstructor
public class StrategyController {
    
    private final TradingStrategyService strategyService;
    
    @GetMapping
    public Result<PageResult<StrategyVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String strategyType,
            @RequestParam(required = false) Integer status,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        PageResult<StrategyVO> result = strategyService.getStrategyList(
                pageNum, pageSize, keyword, strategyType, status, 
                userPrincipal.getUserId(), userPrincipal.getRoleCode());
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    public Result<StrategyVO> detail(@PathVariable Long id) {
        StrategyVO vo = strategyService.getStrategyDetail(id);
        return Result.success(vo);
    }
    
    @PostMapping
    public Result<Void> create(@Valid @RequestBody StrategyCreateRequest request,
                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        strategyService.createStrategy(request, userPrincipal.getUserId());
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @Valid @RequestBody StrategyUpdateRequest request,
                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        strategyService.updateStrategy(id, request, userPrincipal.getUserId(), userPrincipal.getRoleCode());
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        strategyService.deleteStrategy(id, userPrincipal.getUserId(), userPrincipal.getRoleCode());
        return Result.success();
    }
    
    @PostMapping("/{id}/submit")
    public Result<Void> submitForReview(@PathVariable Long id,
                                        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        strategyService.submitForReview(id, userPrincipal.getUserId());
        return Result.success();
    }
    
    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Void> review(@PathVariable Long id,
                               @Valid @RequestBody StrategyReviewRequest request,
                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        strategyService.reviewStrategy(id, request, userPrincipal.getUserId());
        return Result.success();
    }
    
    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Void> start(@PathVariable Long id,
                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        strategyService.startStrategy(id, userPrincipal.getUserId(), userPrincipal.getRoleCode());
        return Result.success();
    }
    
    @PostMapping("/{id}/pause")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Void> pause(@PathVariable Long id,
                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        strategyService.pauseStrategy(id, userPrincipal.getUserId(), userPrincipal.getRoleCode());
        return Result.success();
    }
    
    @PostMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Void> archive(@PathVariable Long id,
                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        strategyService.archiveStrategy(id, userPrincipal.getUserId(), userPrincipal.getRoleCode());
        return Result.success();
    }
}
