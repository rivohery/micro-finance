package com.alibou.finance.customer.application.usecase;

import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.Email;

public interface UpdateEmailCustomerUseCase {
    void execute(UserId userId, Email email);
}
