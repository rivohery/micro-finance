package com.alibou.finance.account.application.utils;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.exception.AccountNotFoundException;
import com.alibou.finance.account.domain.exception.ThirdPartyServiceException;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.linker.CurrencyExchangePort;
import com.alibou.finance.account.domain.vo.transaction.*;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.account.application.port.command.CreateTransactionCommand;
import com.alibou.finance.account.domain.agregate.TransactionTypeEnum;
import com.alibou.finance.shared.domain.vo.Description;
import com.alibou.finance.account.domain.vo.transaction.TransactionCurrencyCode;
import com.alibou.finance.shared.domain.vo.user.OperatorName;
import com.alibou.finance.shared.domain.vo.user.Username;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@RequiredArgsConstructor
public class TransactionOperationExecutor {

    private final AccountRepository accountRepository;
    private final CurrencyExchangePort currencyExchangePort;

    public Account getAccountByAccountNumber(AccountNumber accountNumber){
        return accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new AccountNotFoundException(String.format("Compte introuvable: numéros de compte invalid: %s", accountNumber.value()))
        );
    }

    public FinalAmount convertToFinalAmount(BigDecimal originalAmount, BigDecimal exchangeRate){
        return new FinalAmount(originalAmount.multiply(exchangeRate));
    }

    public CreateTransactionCommand prepareTransactionCommand(
            Account account,
            TransactionCurrencyCode transactionCurrencyCode,
            TransactionTypeEnum transactionTypeEnum,
            BigDecimal exchangeRate,
            OriginalAmount originalAmount,
            Username username,
            SoldBeforeTransaction soldBeforeTransaction,
            Description description
    ){
        TargetCurrencyCode targetCurrencyCode = new TargetCurrencyCode(account.getCurrencyCode().value());
        OperatorName operatorName = new OperatorName(username.value());
        TransactionType transactionType = new TransactionType(transactionTypeEnum);
        return new CreateTransactionCommand(
                account.getAccountId(),
                account.getAccountNumber(),
                transactionType,
                transactionCurrencyCode,
                targetCurrencyCode,
                new ExchangeRate(exchangeRate),
                originalAmount,
                soldBeforeTransaction,
                description,
                operatorName
        );
    }

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency){
        BigDecimal exchangeRate =  currencyExchangePort.getExchangeRate(fromCurrency, toCurrency);
        if(Objects.isNull(exchangeRate)){
            throw new ThirdPartyServiceException("Taux de change null; l'opération ne doit pas continuer");
        }
        return exchangeRate;
    }

    public Account updateAndSaveAccount(Account account){
        BigDecimal exchangeRateToMga = currencyExchangePort.getExchangeRate(account.getCurrencyCode().value(), "MGA");
        account.calculMgaBalance(exchangeRateToMga);
        return accountRepository.save(account);
    }

}
