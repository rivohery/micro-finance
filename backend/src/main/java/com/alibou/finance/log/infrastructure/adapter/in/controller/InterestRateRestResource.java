package com.alibou.finance.log.infrastructure.adapter.in.controller;

import com.alibou.finance.log.domain.agregate.InterestRateTrace;
import com.alibou.finance.account.infrastructure.adapter.in.dto.InterestRateSummaryResp;
import com.alibou.finance.log.infrastructure.adapter.in.dto.InterestRateTraceResponse;
import com.alibou.finance.log.infrastructure.adapter.out.mappers.InterestRateTraceMapper;
import com.alibou.finance.log.infrastructure.transactional.InterestRateUseCaseProxy;
import com.alibou.finance.shared.application.PageResult;
import com.alibou.finance.shared.infrastructure.dto.PageResponse;
import com.alibou.finance.shared.infrastructure.mapper.PageMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/interest-rate-trace")
@Tag(name = "Endpoint-interest-rate-trace", description = "Endpoints pour consulter les traces des taux d'intérêts ajoutés")
@RequiredArgsConstructor
public class InterestRateRestResource {

    private final InterestRateUseCaseProxy interestRateService;

    @GetMapping
    public ResponseEntity<InterestRateSummaryResp>findAllByMonth(
         @RequestParam(name="month", required = false, defaultValue = "") String month,
         @RequestParam(name="page", required = false, defaultValue = "0")int page,
         @RequestParam(name="size", required = false, defaultValue = "10")int size
    ){

        PageResult<InterestRateTrace> pages = interestRateService.findAllMonthlyInterestRateTrace(month, page, size);
        PageResponse<InterestRateTraceResponse>interestRateTraces = PageMapper.toPageResponse(pages, InterestRateTraceMapper::domainToResponse);
        BigDecimal totalInterestRateMonthly = interestRateService.getTotalMonthlyInterestRate(month) != null ?
                interestRateService.getTotalMonthlyInterestRate(month) : BigDecimal.ZERO;
        return ResponseEntity.ok(
                InterestRateSummaryResp.builder()
                        .interestRateTraces(interestRateTraces)
                        .totalInterestRateMonthly(totalInterestRateMonthly)
                        .build()
        );
    }

}

