package com.alibou.finance.shared.domain.event;

import com.alibou.finance.shared.domain.vo.user.Email;
import com.alibou.finance.shared.domain.vo.user.UserId;

public record UserEmailChangedEvent(
        UserId userId,
        Email newEmail
) { }
