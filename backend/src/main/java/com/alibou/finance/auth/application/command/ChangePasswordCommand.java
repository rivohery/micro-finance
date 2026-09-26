package com.alibou.finance.auth.application.command;

import com.alibou.finance.auth.domain.vo.Password;
import com.alibou.finance.shared.domain.vo.user.Username;

public record ChangePasswordCommand(
        Username username,
        Password oldPassword,
        Password newPassword
) {
}
