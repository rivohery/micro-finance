package com.alibou.finance.account.application.service;


import com.alibou.finance.account.application.port.command.CreateTransactionCommand;
import com.alibou.finance.account.application.port.command.WithdrawCommand;
import com.alibou.finance.account.application.port.result.TransactionResult;
import com.alibou.finance.account.application.port.usecase.CreateTransactionUseCase;
import com.alibou.finance.account.application.utils.TransactionOperationExecutor;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.agregate.TransactionTypeEnum;
import com.alibou.finance.account.domain.exception.InsufficientBalanceException;
import com.alibou.finance.account.domain.vo.account.Balance;
import com.alibou.finance.account.domain.vo.transaction.*;
import com.alibou.finance.shared.domain.vo.Description;
import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
import com.alibou.finance.shared.domain.vo.user.OperatorName;
import com.alibou.finance.shared.domain.vo.user.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WithdrawServiceApplicationTest {
    @Mock
    private TransactionOperationExecutor transactionOperationExecutor;
    @Mock
    private CreateTransactionUseCase createTransactionUseCase;

    @InjectMocks
    private WithdrawServiceApplication withdrawService;

    @Captor
    ArgumentCaptor<CreateTransactionCommand> createTransactionCaptor;

    Account account = mock(Account.class);
    Transaction transaction = mock(Transaction.class);

    BigDecimal balance = new BigDecimal("45000");
    BigDecimal exchangeRate = new BigDecimal("4500");
    WithdrawCommand withdrawCommand;

    private void stubAccount(){
        when(account.getBalance()).thenReturn(new Balance(balance));
        when(account.getCurrencyCode()).thenReturn(new CurrencyCode("MGA"));
        when(account.getAccountNumber()).thenReturn(new AccountNumber("010-20-1234567890"));
        when(account.getAccountId()).thenReturn(new AccountId(UUID.randomUUID()));
    }
    @BeforeEach
    void setUp(){
        withdrawCommand = new WithdrawCommand(
                new AccountNumber("010-20-1234567890"),
                new OriginalAmount(new BigDecimal("5")),
                new TransactionCurrencyCode("USD"),
                new Description("test withdraw"),
                new Username("alibou")
        );
    }

    @Test
    void should_processWithdrawSuccessfully(){
        stubAccount();

        when(transactionOperationExecutor.getAccountByAccountNumber(withdrawCommand.accountNumber())).thenReturn(account);
        when(transactionOperationExecutor.getExchangeRate("USD", "MGA")).thenReturn(exchangeRate);
        when(transactionOperationExecutor.convertToFinalAmount(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(new FinalAmount(new BigDecimal("22500")));
        when(transactionOperationExecutor.updateAndSaveAccount(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        when(transactionOperationExecutor.prepareTransactionCommand(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenAnswer(i -> {
                    Account account = i.getArgument(0);
                    TransactionCurrencyCode transactionCurrencyCode = i.getArgument(1);
                    TransactionTypeEnum transactionTypeEnum = i.getArgument(2);
                    BigDecimal exchangeRate = i.getArgument(3);
                    OriginalAmount originalAmount = i.getArgument(4);
                    Username username = i.getArgument(5);
                    SoldBeforeTransaction soldBeforeTransaction = i.getArgument(6);
                    Description description = i.getArgument(7);
                    TargetCurrencyCode targetCurrencyCode = new TargetCurrencyCode(account.getCurrencyCode().value());
                    return new CreateTransactionCommand(
                            account.getAccountId(),
                            account.getAccountNumber(),
                            new TransactionType(transactionTypeEnum),
                            transactionCurrencyCode,
                            targetCurrencyCode,
                            new ExchangeRate(exchangeRate),
                            originalAmount,
                            soldBeforeTransaction,
                            description,
                            new OperatorName(username.value())
                    );
                });
        when(createTransactionUseCase.execute(any(CreateTransactionCommand.class))).thenReturn(transaction);

        TransactionResult result = withdrawService.execute(withdrawCommand);

        assertThat(result).isNotNull();

        verify(account).makeWithdraw(any(FinalAmount.class));
        verify(transactionOperationExecutor).updateAndSaveAccount(any(Account.class));
        verify(createTransactionUseCase).execute(createTransactionCaptor.capture());
        CreateTransactionCommand cmd = createTransactionCaptor.getValue();
        assertThat(cmd.exchangeRate().value()).isEqualByComparingTo(exchangeRate);
        assertThat(cmd.transactionType().value()).isEqualTo(TransactionTypeEnum.WITHDRAWAL);
        assertThat(cmd.originalAmount().value()).isEqualByComparingTo(new BigDecimal("5"));
        assertThat(cmd.transactionCurrencyCode().value()).isEqualTo("USD");
        assertThat(cmd.targetCurrencyCode().value()).isEqualTo("MGA");

    }

    @Test
    void should_throwInsufficientBalanceException(){
        stubAccount();
        when(transactionOperationExecutor.getAccountByAccountNumber(withdrawCommand.accountNumber())).thenReturn(account);
        when(transactionOperationExecutor.getExchangeRate("USD", "MGA")).thenReturn(exchangeRate);
        when(transactionOperationExecutor.convertToFinalAmount(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(new FinalAmount(new BigDecimal("22500")));

        doThrow(new InsufficientBalanceException("Sold Insuffisant"))
                .when(account)
                .makeWithdraw(any(FinalAmount.class));

        assertThatThrownBy(() -> withdrawService.execute(withdrawCommand)).isInstanceOf(InsufficientBalanceException.class);

        verify(transactionOperationExecutor, never()).updateAndSaveAccount(any(Account.class));
        verify(createTransactionUseCase, never()).execute(any());
    }
}
