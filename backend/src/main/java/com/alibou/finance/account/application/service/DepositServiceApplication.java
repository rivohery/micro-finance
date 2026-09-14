package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.dto.command.DepositCommand;
import com.alibou.finance.account.application.port.dto.output.TransactionResult;
import com.alibou.finance.account.application.port.usecase.DepositUseCase;
import com.alibou.finance.account.application.utils.TransactionFactory;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.out.service.ReferenceGenerator;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.log.domain.agregate.TransactionTypeEnum;
import com.alibou.finance.log.domain.vo.transaction.*;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class DepositServiceApplication implements DepositUseCase {
    private final AccountRepository accountRepository;
    private final CurrencyExchangePort currencyExchangePort;
    private final ReferenceGenerator referenceGenerator;
    private final TransactionRepository transactionRepository;

    @Override
    public TransactionResult execute(DepositCommand input) {
        var account = TransactionFactory.getAccountByAccountNumber(accountRepository, input.accountNumber());

        /***Pour le calcul de taux d'intérêt***/
        SoldBeforeTransaction soldBeforeTransaction = SoldBeforeTransaction.getFrom(account.getBalance().value());

        BigDecimal exchangeRate = currencyExchangePort.getExchangeRate(input.transactionCurrencyCode().value(), account.getCurrency().getCode().value());
        FinalAmount finalAmount = TransactionFactory.convertToFinalAmount(input.originalAmount().value(), exchangeRate);
        account.updateBalanceOfDeposit(finalAmount);
        TransactionFactory.updateMgaBalance(currencyExchangePort, account);
        account = accountRepository.save(account);

        Transaction deposit = TransactionFactory.prepareTraceOfTransaction(
                referenceGenerator,
                account.getAccountNumber(),
                input.transactionCurrencyCode(),
                TransactionTypeEnum.DEPOSIT,
                finalAmount,
                exchangeRate,
                account.getCurrency().getCode().value(),
                soldBeforeTransaction,
                input.user(),
                input.description(),
                input.originalAmount()
        );
        deposit = transactionRepository.save(deposit);
        return TransactionResult.builder()
                .account(account)
                .transaction(deposit)
                .build();
    }

}
