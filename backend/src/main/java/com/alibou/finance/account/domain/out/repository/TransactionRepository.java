package com.alibou.finance.account.domain.out.repository;

import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.account.domain.vo.transaction.Reference;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.shared.application.result.PageResult;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    PageResult<Transaction> findAllByAccountNumber(AccountNumber accountNumber, int page, int size);
    Optional<Transaction>findByReference(Reference reference);
    PageResult<Transaction>findAll(int page, int size);
    Transaction save(Transaction transaction);
    PageResult<Transaction> findAllByCreatedDateBetween(LocalDateTime start, LocalDateTime end,int page,int size);
    List<Transaction> findAllByCreatedDateBetween(LocalDateTime start, LocalDateTime end);

    List<Transaction>findMonthlyTransactionsOfAccount(AccountNumber accountNumber, YearMonth month);


}
