package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.usecase.AddMonthlyInterestUseCase;
import com.alibou.finance.log.application.port.usecase.InterestRateUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.log.domain.agregate.InterestRateTrace;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@RequiredArgsConstructor
public class AddMonthlyInterestServiceApplication implements AddMonthlyInterestUseCase {
    private static final String CURRENCY_REFERENCE_CODE = "MGA";
    private final TransactionRepository transactionRepository;
    private final CurrencyExchangePort currencyExchangePort;
    private final InterestRateUseCase interestRateUseCase;

    @Override
    public Account execute(Account account) {
        LocalDate now = LocalDate.now();
        LocalDateTime startMonth = now.with(TemporalAdjusters.firstDayOfMonth()).atTime(LocalTime.MIN);
        LocalDateTime endMonth = now.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        BigDecimal monthlyInterestRate = BigDecimal.ZERO;
        List<Transaction>transactions = transactionRepository.checkMonthlyTransactionOfAccount(account.getAccountNumber(), startMonth, endMonth);
        if(transactions.isEmpty()){
            monthlyInterestRate  = calculInterestRateOfSpecificDays(account, account.getBalance().value(), startMonth, endMonth);
        } else {
            BigDecimal potentialInterestRate;
            Transaction firstTransactionInMonth = transactions.get(0);
            potentialInterestRate = calculInterestRateOfSpecificDays(account, firstTransactionInMonth.getSoldBeforeTransaction().value(), startMonth, firstTransactionInMonth.getCreatedDate());
            monthlyInterestRate = monthlyInterestRate.add(potentialInterestRate);
            for(int i = 0; i < transactions.size() - 1; i++){
                LocalDateTime startDay = transactions.get(i).getCreatedDate();
                LocalDateTime endDay = transactions.get(i + 1).getCreatedDate();
                BigDecimal potentialSold = transactions.get(i + 1).getSoldBeforeTransaction().value();
                potentialInterestRate = calculInterestRateOfSpecificDays(account, potentialSold, startDay, endDay);
                monthlyInterestRate = monthlyInterestRate.add(potentialInterestRate);
            }
            Transaction lastTransactionInMonth = transactions.get(transactions.size() - 1);
            potentialInterestRate = calculInterestRateOfSpecificDays(account, account.getBalance().value(), lastTransactionInMonth.getCreatedDate(), endMonth);
            monthlyInterestRate = monthlyInterestRate.add(potentialInterestRate);
        }
        account.addMonthlyInterestRate(monthlyInterestRate);

        BigDecimal mgaExchangeRate= currencyExchangePort.getExchangeRate(account.getCurrency().getCode().value(), CURRENCY_REFERENCE_CODE);
        account.calculMgaBalance(mgaExchangeRate);

        InterestRateTrace interestRateTrace = InterestRateTrace.prepareToDataBase(account, mgaExchangeRate, monthlyInterestRate);
        interestRateUseCase.save(interestRateTrace);
        return account;
    }

    public BigDecimal calculInterestRateOfSpecificDays(Account account, BigDecimal potentialSold, LocalDateTime start, LocalDateTime end){
        long nbrDays = calculNbrDaysBetween(start, end);
        return account.calculateInterestRateForSpecificDays(potentialSold, nbrDays);
    }

    public long calculNbrDaysBetween(LocalDateTime start, LocalDateTime end){
        return ChronoUnit.DAYS.between(start, end);
    }
}
