package com.alibou.finance.account.infrastructure.adapter.in.controller;

import com.alibou.finance.account.application.port.usecase.InterestRateUseCase;
import com.alibou.finance.account.domain.agregate.InterestRateTrace;
import com.alibou.finance.account.infrastructure.adapter.in.dto.InterestRateSummaryResp;
import com.alibou.finance.account.infrastructure.adapter.in.dto.InterestRateTraceResponse;
import com.alibou.finance.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/interest-rate-trace")
@Tag(name = "Endpoint-interest-rate-trace", description = "Endpoints pour consulter les traces des taux d'intérêts ajoutés")
@RequiredArgsConstructor
public class InterestRateRestResource {

    private final InterestRateUseCase interestRateService;

    @GetMapping
    public ResponseEntity<InterestRateSummaryResp>findAllByMonth(
         @RequestParam(name="month", required = false, defaultValue = "") String month,
         @RequestParam(name="page", required = false, defaultValue = "0")int page,
         @RequestParam(name="size", required = false, defaultValue = "10")int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<InterestRateTrace>pages = interestRateService.findAllMonthlyInterestRateTrace(month, pageable);
        List<InterestRateTraceResponse>content = pages
                .getContent()
                .stream()
                .map(InterestRateTraceResponse::buildFromDomain)
                .toList();
        PageResponse<InterestRateTraceResponse>interestRateTraces = new PageResponse<>(
                content,
                pages.getNumber(),
                pages.getSize(),
                pages.getTotalElements(),
                pages.getTotalPages(),
                pages.isFirst(),
                pages.isLast()
        );
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

