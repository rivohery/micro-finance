package com.alibou.finance.account.infrastructure.adapter.out.port.linker;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.out.linker.CurrencyExchangePort;
import com.alibou.finance.account.domain.out.linker.HistoryTraceLinker;
import com.alibou.finance.history.HistoryTraceProvider;
import com.alibou.finance.shared.application.command.InterestTraceRateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class HistoryTraceLinkerAdapter implements HistoryTraceLinker {
    private static final String CURRENCY_REFERENCE_CODE = "MGA";
    private final CurrencyExchangePort currencyExchangePort;
    private final HistoryTraceProvider historyTraceProvider;
    @Override
    public void saveMonthlyInterestRate(Account account, BigDecimal monthlyInterest) {
        BigDecimal mgaExchangeRate = currencyExchangePort.getExchangeRate(
                account.getCurrencyCode().value(),
                CURRENCY_REFERENCE_CODE
        );
        BigDecimal mgaAmount = monthlyInterest.multiply(mgaExchangeRate);
        InterestTraceRateCommand cmd = new InterestTraceRateCommand(
             account.getAccountNumber(),
             account.getAnnualInterestRate(),
             account.getCurrencyCode(),
             monthlyInterest,
             mgaExchangeRate,
             mgaAmount
        );
        historyTraceProvider.saveMonthlyInterestRate(cmd);
    }
}
