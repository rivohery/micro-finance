package com.alibou.finance.account.domain.out.linker;

import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.CustomerId;

public interface CustomerLinkerPort {
    CustomerId findCustomerIdByUserId(UserId userId);
    boolean verifyIfCustomerIsActive(CustomerId customerId);
}
