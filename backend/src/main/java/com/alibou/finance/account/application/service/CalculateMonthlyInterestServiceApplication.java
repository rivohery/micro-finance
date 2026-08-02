package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.usecase.CalculateMonthlyInterestUseCase;
import com.alibou.finance.account.application.port.usecase.InterestRateUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.InterestRateTrace;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.vo.interestRate.Amount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculateMonthlyInterestServiceApplication implements CalculateMonthlyInterestUseCase {
    private static final String CURRENCY_REFERENCE_CODE = "MGA";

    private final TransactionRepository transactionRepository;
    private final CurrencyExchangePort currencyExchangePort;
    private final InterestRateUseCase interestRateUseCase;

    @Override
    @Transactional
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
            //calcul du taux avant la premiere transaction
            Transaction firstTransactionInMonth = transactions.get(0);
            potentialInterestRate = calculInterestRateOfSpecificDays(account, firstTransactionInMonth.getSoldBeforeTransaction().value(), startMonth, firstTransactionInMonth.getCreatedDate());
            //System.out.println("potentialInterestRate: " + potentialInterestRate);
            monthlyInterestRate = monthlyInterestRate.add(potentialInterestRate);
            //System.out.println("monthlyInterestRate: " + monthlyInterestRate);
            //System.out.println("================");
            //calcul du taux entre les transactions
            for(int i = 0; i < transactions.size() - 1; i++){
                LocalDateTime startDay = transactions.get(i).getCreatedDate();
                LocalDateTime endDay = transactions.get(i + 1).getCreatedDate();
                BigDecimal potentialSold = transactions.get(i + 1).getSoldBeforeTransaction().value();
                potentialInterestRate = calculInterestRateOfSpecificDays(account, potentialSold, startDay, endDay);
                monthlyInterestRate = monthlyInterestRate.add(potentialInterestRate);
                //System.out.println("potentialInterestRate: " + potentialInterestRate);
                //System.out.println("monthlyInterestRate: " + monthlyInterestRate);
                //System.out.println("================");
            }
            //calcul du taux après la dernière transaction
            Transaction lastTransactionInMonth = transactions.get(transactions.size() - 1);
            potentialInterestRate = calculInterestRateOfSpecificDays(account, account.getBalance().value(), lastTransactionInMonth.getCreatedDate(), endMonth);
            monthlyInterestRate = monthlyInterestRate.add(potentialInterestRate);
            //System.out.println("potentialInterestRate: " + potentialInterestRate);
            //System.out.println("monthlyInterestRate: " + monthlyInterestRate);
            //System.out.println("================");
        }
        System.out.println("monthlyInterestRate final: " + monthlyInterestRate);
        account.addMonthlyInterestRate(monthlyInterestRate);

        BigDecimal mgaExchangeRate= currencyExchangePort.getExchangeRate(account.getCurrency().getCode().value(), CURRENCY_REFERENCE_CODE);
        account.calculMgaBalance(mgaExchangeRate);
        //Spring batch assure la sauvegarde de l'objet Account modifié en BD se fait avec

        InterestRateTrace interestRateTrace = InterestRateTrace.prepareToDataBase(account, mgaExchangeRate, monthlyInterestRate);
        interestRateUseCase.save(interestRateTrace);
        return account;
    }

    public BigDecimal calculInterestRateOfSpecificDays(Account account, BigDecimal potentialSold, LocalDateTime start, LocalDateTime end){
        long nbrDays = calculNbrDaysBetween(start, end);
        //System.out.println("nbrDays: " + nbrDays);
        return account.calculateInterestRateForSpecificDays(potentialSold, nbrDays);
    }

    public long calculNbrDaysBetween(LocalDateTime start, LocalDateTime end){
        return ChronoUnit.DAYS.between(start, end);
    }
}
