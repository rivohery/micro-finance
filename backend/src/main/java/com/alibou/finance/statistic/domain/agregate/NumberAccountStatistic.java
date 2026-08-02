package com.alibou.finance.statistic.domain.agregate;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NumberAccountStatistic {
    private String accountType;
    private Long nbrAccountByType;
}
