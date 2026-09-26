package com.alibou.finance.auth.application.command;

import com.alibou.finance.auth.domain.agregate.RoleEnum;
import com.alibou.finance.shared.domain.vo.user.Username;
import com.alibou.finance.shared.domain.vo.user.Email;

public record CreateUserCommand(
        Username username,
        Email email,
        RoleEnum roleEnum
) {
}
