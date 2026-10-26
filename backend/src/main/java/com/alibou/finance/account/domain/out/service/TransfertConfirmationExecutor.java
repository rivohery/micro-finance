package com.alibou.finance.account.domain.out.service;

import com.alibou.finance.account.domain.out.service.TransfertConfirmationInfo;

public interface TransfertConfirmationExecutor {
    void sendTransactionConfirmation(TransfertConfirmationInfo transfertConfirmationInfo);
}
