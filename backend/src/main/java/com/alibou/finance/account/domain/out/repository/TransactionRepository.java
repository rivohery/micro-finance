package com.alibou.finance.account.domain.out.repository;

import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.account.domain.vo.transaction.Reference;
import com.alibou.finance.account.domain.agregate.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Page<Transaction>findAllByAccountNumber(AccountNumber accountNumber, Pageable pageable);
    Optional<Transaction>findByReference(Reference reference);
    Page<Transaction>findAll(Pageable pageable);
    Transaction save(Transaction transaction);
    Page<Transaction> findAllByCreatedDateBetween(LocalDateTime start, LocalDateTime end,Pageable pageable);
    List<Transaction> findAllByCreatedDateBetween(LocalDateTime start, LocalDateTime end);

    List<Transaction>checkMonthlyTransactionOfAccount(AccountNumber accountNumber, LocalDateTime startMonth, LocalDateTime endMonth);

}
