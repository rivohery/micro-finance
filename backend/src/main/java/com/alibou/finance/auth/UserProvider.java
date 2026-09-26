package com.alibou.finance.auth;

import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.Username;
import com.alibou.finance.shared.domain.vo.user.Email;
import org.springframework.security.core.Authentication;

public interface UserProvider {
    UserInfo createUserFromCustomerInfo(Username username, Email email);
    UserInfo getUserByUserId(UserId userId);
    void disableUser(UserId userId);

    UserInfo getUserConnected(Authentication authentication);
}
