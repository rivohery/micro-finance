package com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection;

public interface NumberAccountStatisticProj {
    String getAccountType();
    Long getNbrAccountByType();
}
