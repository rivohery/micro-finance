package com.alibou.finance.shared.infrastructure.dto;

public record NumberAccountStatistic(
        String accountType,
        Long nbrAccountByType
) { }
