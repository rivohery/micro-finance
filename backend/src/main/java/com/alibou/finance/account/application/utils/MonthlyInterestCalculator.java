package com.alibou.finance.account.application.utils;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

public class MonthlyInterestCalculator {
    public BigDecimal calculate(Account account,
                                List<Transaction> transactions,
                                YearMonth month) {

        LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = month.atEndOfMonth().atTime(LocalTime.MAX);

        if (transactions.isEmpty()) {
            return calculateForPeriod(
                    account,
                    account.getBalance().value(),
                    startOfMonth,
                    endOfMonth
            );
        }

        List<Transaction> sortedTransactions = transactions.stream()
                .sorted(Comparator.comparing(Transaction::getCreatedDate))
                .toList();

        BigDecimal totalInterest = BigDecimal.ZERO;
        LocalDateTime periodStart = startOfMonth;

        for (Transaction transaction : sortedTransactions) {
            BigDecimal balanceBeforeTransaction = transaction
                    .getSoldBeforeTransaction()
                    .value();

            totalInterest = totalInterest.add(
                    calculateForPeriod(
                            account,
                            balanceBeforeTransaction,
                            periodStart,
                            transaction.getCreatedDate()
                    )
            );

            periodStart = transaction.getCreatedDate();
        }
        /* between the last transaction and endOfMonth */
        totalInterest = totalInterest.add(
                calculateForPeriod(
                        account,
                        account.getBalance().value(),
                        periodStart,
                        endOfMonth
                )
        );

        return totalInterest;
    }

    private BigDecimal calculateForPeriod(Account account,
                                          BigDecimal potentialSold,
                                          LocalDateTime start,
                                          LocalDateTime end) {

        long numberOfDays = ChronoUnit.DAYS.between(
                start.toLocalDate(),
                end.toLocalDate()
        );

        return account.calculateInterestAmount( potentialSold, numberOfDays);
    }
}
