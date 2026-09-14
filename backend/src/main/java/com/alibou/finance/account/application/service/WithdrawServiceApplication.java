package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.dto.command.WithdrawCommand;
import com.alibou.finance.account.application.port.dto.output.TransactionResult;
import com.alibou.finance.account.application.port.usecase.WithdrawUseCase;
import com.alibou.finance.account.application.utils.TransactionFactory;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.out.service.ReferenceGenerator;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.log.domain.agregate.TransactionTypeEnum;
import com.alibou.finance.log.domain.vo.transaction.FinalAmount;
import com.alibou.finance.log.domain.vo.transaction.SoldBeforeTransaction;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class WithdrawServiceApplication implements WithdrawUseCase {

    private final AccountRepository accountRepository;
    private final CurrencyExchangePort currencyExchangePort;
    private final ReferenceGenerator referenceGenerator;
    private final TransactionRepository transactionRepository;

    @Override
    public TransactionResult execute(WithdrawCommand input) {
        Account account = TransactionFactory.getAccountByAccountNumber(accountRepository, input.accountNumber());

        SoldBeforeTransaction soldBeforeTransaction = SoldBeforeTransaction.getFrom(account.getBalance().value());

        BigDecimal exchangeRate = currencyExchangePort.getExchangeRate(input.transactionCurrencyCode().value(), account.getCurrency().getCode().value());
        FinalAmount finalAmount = TransactionFactory.convertToFinalAmount(input.originalAmount().value(), exchangeRate);

        account.updateBalanceOfWithdraw(finalAmount);
        TransactionFactory.updateMgaBalance(currencyExchangePort, account);
        account = accountRepository.save(account);

        Transaction withdraw = TransactionFactory.prepareTraceOfTransaction(
                referenceGenerator,
                account.getAccountNumber(),
                input.transactionCurrencyCode(),
                TransactionTypeEnum.WITHDRAWAL,
                finalAmount,
                exchangeRate,
                account.getCurrency().getCode().value(),
                soldBeforeTransaction,
                input.user(),
                input.description(),
                input.originalAmount()
        );
        withdraw = transactionRepository.save(withdraw);
        return TransactionResult.builder()
                .account(account)
                .transaction(withdraw)
                .build();
    }
}
