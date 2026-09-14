package com.alibou.finance.customer.infrastructure.adapter.out.persistence.projection;

import java.time.LocalDate;

public interface RegistrationStatisticProj {
    LocalDate getCreatedDate();
    Long getNbrCustomer();
}
