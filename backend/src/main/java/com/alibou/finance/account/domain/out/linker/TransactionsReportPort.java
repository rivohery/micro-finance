package com.alibou.finance.account.domain.out.linker;

import com.alibou.finance.account.domain.agregate.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionsReportPort {
    byte[]reportTransactions(List<Transaction>transactions, LocalDate createdDate);
}
