package com.alibou.finance.log.application.port.usecase;

import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.log.domain.vo.transaction.Reference;
import com.alibou.finance.shared.application.PageResult;

import java.time.LocalDate;

public interface TransactionConsultationUseCase {
    PageResult<Transaction> findAllByAccountNumber(AccountNumber accountNumber, int page, int size);
    PageResult<Transaction>findAllByCreatedDate(LocalDate createdDate, int page, int size);
    Transaction findByReference(Reference reference);
    byte[]exportToPdf(LocalDate createdDate);
}
