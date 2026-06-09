package com.trading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trading.entity.TradingStrategy;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TradingStrategyMapper extends BaseMapper<TradingStrategy> {
    
    @Select("SELECT s.*, u.username as creator_name, r.username as reviewer_name " +
            "FROM trading_strategy s " +
            "LEFT JOIN sys_user u ON s.creator_id = u.id " +
            "LEFT JOIN sys_user r ON s.reviewer_id = r.id " +
            "WHERE s.id = #{id}")
    TradingStrategy selectByIdWithCreator(@Param("id") Long id);
    
    @Select("SELECT s.*, u.username as creator_name, r.username as reviewer_name " +
            "FROM trading_strategy s " +
            "LEFT JOIN sys_user u ON s.creator_id = u.id " +
            "LEFT JOIN sys_user r ON s.reviewer_id = r.id " +
            "ORDER BY s.create_time DESC")
    List<TradingStrategy> selectListWithCreator();
}
