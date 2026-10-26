package com.alibou.finance.auth;

import com.alibou.finance.shared.domain.vo.user.Email;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.Username;

public record UserInfo(
        UserId userId,
        Username username,
        Email userEmail
) {
}
