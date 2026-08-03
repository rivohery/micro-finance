package com.alibou.finance.shared.application;

import com.alibou.finance.shared.domain.Assert;

import java.util.List;

public record PageResult<T>(
        List<T>content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast
) {
   public PageResult {
       Assert.field("pageNumber",this.pageNumber()).positive();
       Assert.field("pageSize",this.pageSize()).positive();
       Assert.field("totalElements",this.totalElements()).positive();
       Assert.field("totalPages",this.totalPages()).positive();
   }
}
