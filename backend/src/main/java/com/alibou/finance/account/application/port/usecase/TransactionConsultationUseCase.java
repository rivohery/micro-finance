package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.vo.transaction.Reference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TransactionConsultationUseCase {
    Page<Transaction> findAllByAccountNumber(AccountNumber accountNumber, Pageable pageable);
    Page<Transaction>findAllByCreatedDate(LocalDate createdDate, Pageable pageable);
    Transaction findByReference(Reference reference);
    byte[]exportToPdf(LocalDate createdDate);
}
