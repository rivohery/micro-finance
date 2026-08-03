package com.alibou.finance.shared.infrastructure.mapper;

import com.alibou.finance.shared.application.PageResult;
import com.alibou.finance.shared.infrastructure.dto.PageResponse;
import org.springframework.data.domain.Page;

import java.util.function.Function;

public class PageMapper {

    public static <E, D> PageResult<D> toPageResult(Page<E> springPage, Function<E, D> entityToDomainMapper) {
        return new PageResult<>(
                springPage.getContent().stream().map(entityToDomainMapper).toList(),
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages(),
                springPage.isFirst(),
                springPage.isLast()
        );
    }

    public static <E, D>PageResponse<D> toPageResponse(PageResult<E>page, Function<E, D>domainToResponseMapper) {
        return new PageResponse<>(
                page.content().stream().map(domainToResponseMapper).toList(),
                page.pageNumber(),
                page.pageSize(),
                page.totalElements(),
                page.totalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
