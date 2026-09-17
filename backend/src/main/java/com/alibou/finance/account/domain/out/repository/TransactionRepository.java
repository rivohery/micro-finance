package com.alibou.finance.account.domain.out.repository;

import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.history.domain.vo.transaction.Reference;
import com.alibou.finance.history.domain.agregate.Transaction;
import com.alibou.finance.shared.application.PageResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    PageResult<Transaction> findAllByAccountNumber(AccountNumber accountNumber, int page, int size);
    Optional<Transaction>findByReference(Reference reference);
    PageResult<Transaction>findAll(int page, int size);
    Transaction save(Transaction transaction);
    PageResult<Transaction> findAllByCreatedDateBetween(LocalDateTime start, LocalDateTime end,int page,int size);
    List<Transaction> findAllByCreatedDateBetween(LocalDateTime start, LocalDateTime end);

    List<Transaction>checkMonthlyTransactionOfAccount(AccountNumber accountNumber, LocalDateTime startMonth, LocalDateTime endMonth);

}
