package com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection;

import java.math.BigDecimal;

public interface SoldeAccountStatisticProj {
    String getAccountType();
    BigDecimal getSoldeAccountByType();


}
