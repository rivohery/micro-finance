package com.alibou.finance.customer.infrastructure.adapter.out.linker;

import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.auth.UserProvider;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.Username;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.out.linker.UserLinkerPort;
import com.alibou.finance.shared.domain.vo.user.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLinkerPortAdapter implements UserLinkerPort {
    private final UserProvider userProvider;

    @Override
    public UserInfo createUserFromCustomerInfo(Username username, Email email) {
        return userProvider.createUserFromCustomerInfo(username, email);
    }

    @Override
    public UserInfo getUserByCustomer(Customer customer) {
        return userProvider.getUserByUserId(customer.getUserId());
    }

    @Override
    public void disableUser(UserId userId) {
        userProvider.disableUser(userId);
    }
}
