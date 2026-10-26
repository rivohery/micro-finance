package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.vo.transaction.Reference;
import com.alibou.finance.shared.application.result.PageResult;

import java.time.LocalDate;

public interface TransactionConsultationUseCase {
    PageResult<Transaction> findAllByAccountNumber(AccountNumber accountNumber, int page, int size);
    PageResult<Transaction>findAllByCreatedDate(LocalDate createdDate, int page, int size);
    Transaction findByReference(Reference reference);
    byte[]exportToPdf(LocalDate createdDate);
}
