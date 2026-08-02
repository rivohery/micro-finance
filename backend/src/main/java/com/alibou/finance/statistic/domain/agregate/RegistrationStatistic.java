package com.alibou.finance.statistic.domain.agregate;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegistrationStatistic {
    private String dayOfWeek;
    private Long nbrCustomer;
}
