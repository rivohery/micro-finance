package com.alibou.finance.account.domain.out.service;

import com.alibou.finance.log.domain.agregate.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionsReportPort {
    byte[]reportTransactions(List<Transaction>transactions, LocalDate createdDate);
}
