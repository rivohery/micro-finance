package com.alibou.finance.customer.domain.out.linker;

import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.Username;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.shared.domain.vo.user.Email;

public interface UserLinkerPort {
    UserInfo createUserFromCustomerInfo(Username username, Email email);
    UserInfo getUserByCustomer(Customer customer);
    void disableUser(UserId userId);
}
