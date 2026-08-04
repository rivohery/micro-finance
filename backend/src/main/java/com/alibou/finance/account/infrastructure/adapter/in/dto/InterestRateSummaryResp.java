package com.alibou.finance.account.infrastructure.adapter.in.dto;

import com.alibou.finance.log.infrastructure.adapter.in.dto.InterestRateTraceResponse;
import com.alibou.finance.shared.infrastructure.dto.PageResponse;
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
