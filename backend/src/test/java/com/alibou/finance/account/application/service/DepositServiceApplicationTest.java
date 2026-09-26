package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.command.CreateTransactionCommand;
import com.alibou.finance.account.application.port.command.DepositCommand;
import com.alibou.finance.account.application.port.result.TransactionResult;
import com.alibou.finance.account.application.port.usecase.CreateTransactionUseCase;
import com.alibou.finance.account.application.utils.TransactionOperationExecutor;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.agregate.TransactionTypeEnum;
import com.alibou.finance.account.domain.exception.AccountNotFoundException;
import com.alibou.finance.account.domain.exception.ThirdPartyServiceException;
import com.alibou.finance.account.domain.vo.account.AccountStatus;
import com.alibou.finance.account.domain.vo.account.Balance;
import com.alibou.finance.account.domain.vo.account.MgaBalance;
import com.alibou.finance.account.domain.vo.transaction.*;
import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import com.alibou.finance.shared.domain.vo.Description;
import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
import com.alibou.finance.shared.domain.vo.user.OperatorName;
import com.alibou.finance.shared.domain.vo.user.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepositServiceApplicationTest {

    @Mock
    private TransactionOperationExecutor transactionOperationExecutor;
    @Mock
    private CreateTransactionUseCase createTransactionUseCase;

    @InjectMocks
    private DepositServiceApplication depositService;

    DepositCommand depositCommand;

    Account account;
    Transaction transaction;

    @BeforeEach
    void setUp() {
        depositCommand = new DepositCommand(
                new AccountNumber("010-20-1234567890"),
                new OriginalAmount(new BigDecimal("10")),
                new TransactionCurrencyCode("USD"),
                new Description("test"),
                new Username("alibou")
        );
        transaction = Transaction.builder().transactionId(TransactionId.generate()).build();
    }

    private void buildAccount(String balance, String mgaBalance, String currencyCode){
        account =  Account.builder()
                        .accountNumber(new AccountNumber("010-20-1234567890"))
                        .accountId(AccountId.generate())
                        .accountStatus(new AccountStatus(AccountStatusEnum.ACTIVE))
                        .balance(new Balance(new BigDecimal(balance)))
                        .mgaBalance(new MgaBalance(new BigDecimal(mgaBalance)))
                        .currencyCode(new CurrencyCode(currencyCode))
                        .build();
    }

    @Test
    @DisplayName("execute() - doit effectuer un dépôt plus details avec succès")
    void deposit_shouldShowDetailsOfDepositSuccessfully() {
        buildAccount("300", "300","MGA");

        when(transactionOperationExecutor.getAccountByAccountNumber(depositCommand.accountNumber())).thenReturn(account);
        when(transactionOperationExecutor.getExchangeRate("USD", "MGA")).thenReturn(new BigDecimal("4500"));
        when(transactionOperationExecutor.convertToFinalAmount(any(BigDecimal.class), any(BigDecimal.class))).thenAnswer(i -> {
            BigDecimal originalAmount = i.getArgument(0);
            BigDecimal exchangeRate = i.getArgument(1);
            return new FinalAmount(originalAmount.multiply(exchangeRate));
        });
        when(transactionOperationExecutor.updateAndSaveAccount(any(Account.class))).thenAnswer(i -> {
            Account account = i.getArgument(0);
            account.calculMgaBalance(BigDecimal.ONE);
            return account;
        });
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
        });
        when(createTransactionUseCase.execute(any(CreateTransactionCommand.class))).thenAnswer(i -> {
            CreateTransactionCommand command = i.getArgument(0);
            Reference reference = new Reference("TR-10");
            return Transaction.initializeNewTransaction(
                command.transactionCurrencyCode(),
                command.targetCurrencyCode(),
                command.soldBeforeTransaction(),
                command.transactionType(),
                command.accountId(),
                command.accountNumber(),
                command.description(),
                command.exchangeRate(),
                command.originalAmount(),
                command.operatorName(),
                new FinalAmount(command.originalAmount().value().multiply(command.exchangeRate().value())),
                reference
            );
        });


        TransactionResult result = depositService.execute(depositCommand);

        assertThat(result).isNotNull();
        assertThat(result.account().getAccountNumber()).isEqualTo(new AccountNumber("010-20-1234567890"));
        assertThat(result.account().getBalance().value()).isEqualByComparingTo(BigDecimal.valueOf(10 * 4500 + 300));
        assertThat(result.account().getMgaBalance().value()).isEqualByComparingTo(BigDecimal.valueOf(10 * 4500 + 300));

        assertThat(result.transaction()).isNotNull();
        assertThat(result.transaction().getAccountNumber()).isEqualTo(account.getAccountNumber());
        assertThat(result.transaction().getSoldBeforeTransaction().value()).isEqualByComparingTo(new BigDecimal("300"));
        assertThat(result.transaction().getOriginalAmount().value()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(result.transaction().getFinalAmount().value()).isEqualByComparingTo(new BigDecimal("45000"));
        assertThat(result.transaction().getExchangeRate().value()).isEqualByComparingTo(new BigDecimal("4500"));
        assertThat(result.transaction().getTargetCurrencyCode()).isEqualTo(new TargetCurrencyCode("MGA"));
        assertThat(result.transaction().getTransactionCurrencyCode()).isEqualTo(new TransactionCurrencyCode("USD"));

        verify(transactionOperationExecutor).convertToFinalAmount(any(BigDecimal.class), any(BigDecimal.class));
        verify(createTransactionUseCase).execute(any(CreateTransactionCommand.class));
    }

    @Test
    @DisplayName("execute() - doit lever une exception si le compte est introuvable")
    void execute_shouldThrowWhenAccountNotFound() {
        when(transactionOperationExecutor.getAccountByAccountNumber(depositCommand.accountNumber()))
                .thenThrow(new AccountNotFoundException("Compte introuvable"));

        assertThatThrownBy(() -> depositService.execute(depositCommand))
                .isInstanceOf(AccountNotFoundException.class);

        verify(transactionOperationExecutor, never()).updateAndSaveAccount(any(Account.class));
        verifyNoInteractions(createTransactionUseCase);
    }

    @Test
    @DisplayName("execute() - doit lever une exception si un taux de change null est retourné")
    void execute_shouldThrowExceptionWhenProblemOfThirdServiceExchangeRate() {
        buildAccount("300", "300","MGA");

        when(transactionOperationExecutor.getAccountByAccountNumber(depositCommand.accountNumber())).thenReturn(account);
        when(transactionOperationExecutor.getExchangeRate(anyString(), anyString()))
                .thenThrow(new ThirdPartyServiceException("Taux de change null; l'opération ne doit pas continuer"));

        assertThatThrownBy(() -> depositService.execute(depositCommand))
                .isInstanceOf(ThirdPartyServiceException.class);

        verify(transactionOperationExecutor, never()).convertToFinalAmount(any(BigDecimal.class), any(BigDecimal.class));
        verify(transactionOperationExecutor, never()).updateAndSaveAccount(any(Account.class));
        verifyNoInteractions(createTransactionUseCase);
    }
}
