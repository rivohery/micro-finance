package com.alibou.finance.account.domain.out.service;

import com.alibou.finance.account.domain.out.service.dto.TransfertConfirmationInfo;

public interface TransfertConfirmationPort {
    int sendTransactionConfirmation(TransfertConfirmationInfo transfertConfirmationInfo);
}
