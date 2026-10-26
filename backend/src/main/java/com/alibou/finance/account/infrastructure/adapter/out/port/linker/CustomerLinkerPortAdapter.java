package com.alibou.finance.account.infrastructure.adapter.out.port.linker;

import com.alibou.finance.account.domain.out.linker.CustomerLinkerPort;
import com.alibou.finance.customer.CustomerProvider;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerLinkerPortAdapter implements CustomerLinkerPort {

   private final CustomerProvider customerProvider;

    @Override
    public CustomerId findCustomerIdByUserId(UserId userId) {
        return customerProvider.findCustomerIdByUserId(userId);
    }

    @Override
    public boolean verifyIfCustomerIsActive(CustomerId customerId) {
        return customerProvider.verifyIfCustomerIsActive(customerId);
    }
}
