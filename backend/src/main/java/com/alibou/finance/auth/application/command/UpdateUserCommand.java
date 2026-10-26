package com.alibou.finance.auth.application.command;

import com.alibou.finance.auth.domain.vo.Password;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.Username;
import com.alibou.finance.shared.domain.vo.user.Email;


public record UpdateUserCommand (
        UserId userId,
        Username username,
        Email email,
        Password password
){ }
