package com.trading.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResult<T> {
    
    private List<T> list;
    private Long total;
    private Long pageNum;
    private Long pageSize;
    private Long totalPages;
}
