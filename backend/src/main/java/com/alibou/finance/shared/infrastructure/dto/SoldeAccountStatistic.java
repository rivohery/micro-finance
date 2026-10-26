package com.alibou.finance.shared.infrastructure.dto;

import java.math.BigDecimal;

public record SoldeAccountStatistic(
        String accountType,
        BigDecimal soldeAccountByType
) { }
