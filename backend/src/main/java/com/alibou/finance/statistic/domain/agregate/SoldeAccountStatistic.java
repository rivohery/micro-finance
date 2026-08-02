package com.alibou.finance.statistic.domain.agregate;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class SoldeAccountStatistic {
    private String accountType;
    private BigDecimal soldeAccountByType;
}
