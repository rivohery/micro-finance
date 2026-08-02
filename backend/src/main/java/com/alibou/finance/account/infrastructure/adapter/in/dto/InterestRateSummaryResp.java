package com.alibou.finance.account.infrastructure.adapter.in.dto;

import com.alibou.finance.shared.dto.PageResponse;
import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InterestRateSummaryResp {
    PageResponse<InterestRateTraceResponse>interestRateTraces;
    BigDecimal totalInterestRateMonthly;
}
