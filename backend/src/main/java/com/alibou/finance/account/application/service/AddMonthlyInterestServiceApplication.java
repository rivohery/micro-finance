package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.usecase.AddMonthlyInterestUseCase;
import com.alibou.finance.account.application.utils.MonthlyInterestCalculator;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.out.linker.HistoryTraceLinker;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;

@RequiredArgsConstructor
public class AddMonthlyInterestServiceApplication implements AddMonthlyInterestUseCase {

    private final TransactionRepository transactionRepository;
    private final HistoryTraceLinker historyTraceLinker;
    private final MonthlyInterestCalculator monthlyInterestCalculator;
    private final Clock clock;

    @Override
    public Account execute(Account account) {
        YearMonth currentMonth = YearMonth.now(clock);

        List<Transaction> transactions = transactionRepository.findMonthlyTransactionsOfAccount(account.getAccountNumber(), currentMonth);

        BigDecimal monthlyInterest = monthlyInterestCalculator.calculate(account, transactions, currentMonth);
        account.addMonthlyInterestRate(monthlyInterest);
        historyTraceLinker.saveMonthlyInterestRate(account, monthlyInterest);
        return account;
    }
}
