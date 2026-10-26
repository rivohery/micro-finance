package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.command.CreateTransactionCommand;
import com.alibou.finance.account.application.port.command.TransferCommand;
import com.alibou.finance.account.application.port.result.TransferResult;
import com.alibou.finance.account.application.port.usecase.CreateTransactionUseCase;
import com.alibou.finance.account.application.utils.TransactionOperationExecutor;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.agregate.TransactionTypeEnum;
import com.alibou.finance.account.domain.exception.InsufficientBalanceException;
import com.alibou.finance.account.domain.out.linker.CustomerLinkerPort;
import com.alibou.finance.account.domain.out.service.TransfertConfirmationExecutor;
import com.alibou.finance.account.domain.out.service.TransfertConfirmationInfo;
import com.alibou.finance.account.domain.vo.account.AccountStatus;
import com.alibou.finance.account.domain.vo.account.Balance;
import com.alibou.finance.account.domain.vo.account.OverdraftLimit;
import com.alibou.finance.account.domain.vo.transaction.*;
import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import com.alibou.finance.shared.domain.exception.OperationNotPermittedException;
import com.alibou.finance.shared.domain.vo.Description;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
import com.alibou.finance.shared.domain.vo.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceApplicationTest {
    @Mock
    private  TransactionOperationExecutor transactionOperationExecutor;
    @Mock
    private  CustomerLinkerPort customerLinkerPort;
    @Mock
    private  CreateTransactionUseCase createTransactionUseCase;
    @Mock
    private  TransfertConfirmationExecutor transfertConfirmationPort;
    @InjectMocks
    private TransferServiceApplication transferServiceApplication;

    Account sourceAccount;
    Account targetAccount;
    Balance sourceBalance = new Balance(new BigDecimal("50"));
    Balance targetBalance = new Balance(new BigDecimal("1000"));
    BigDecimal mgaExchangeRate = new BigDecimal("5000");//USD->MGA
    UserId userId = UserId.generate();
    CustomerId customerId = CustomerId.generate();
    UserInfo userInfo = mock(UserInfo.class);

    TransferCommand transferCommand;

    @Captor
    ArgumentCaptor<TransfertConfirmationInfo>transfertCaptor;


    private void stubCreateTransactionCommand(){
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
    }

    private void stubTransactionHistoric(){
        when(createTransactionUseCase.execute(any(CreateTransactionCommand.class))).thenAnswer(i -> {
            CreateTransactionCommand command = i.getArgument(0);
            FinalAmount finalAmount = new FinalAmount(command.originalAmount().value().multiply(command.exchangeRate().value()));
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
                    finalAmount,
                    new Reference("TR-01")
            );
        });
    }

    @BeforeEach
    void setUp(){
        sourceAccount = Account.builder()
                .accountNumber(new AccountNumber("010-20-1234567890"))
                .balance(sourceBalance)
                .currencyCode(new CurrencyCode("USD"))
                .customerId(customerId)
                .accountStatus(new AccountStatus(AccountStatusEnum.ACTIVE))
                .overdraftLimit(new OverdraftLimit(new BigDecimal("-5")))
                .build();
        targetAccount = Account.builder()
                .accountNumber(new AccountNumber("010-10-1234567890"))
                .balance(targetBalance)
                .currencyCode(new CurrencyCode("MGA"))
                .accountStatus(new AccountStatus(AccountStatusEnum.ACTIVE))
                .build();

    }


    @Test
    void transfer_shouldProcessSuccessfully(){
        transferCommand = new TransferCommand(
                new AccountNumber("010-20-1234567890"),
                new AccountNumber("010-10-1234567890"),
                new OriginalAmount(new BigDecimal("10")),
                new Description("Test transfer"),
                userInfo
        );

        when(userInfo.userId()).thenReturn(userId);
        when(userInfo.username()).thenReturn(new Username("alibou"));
        when(userInfo.userEmail()).thenReturn(new Email("alibou@gmail.com"));

        //-----processWithdraw-----
        when(transactionOperationExecutor.getAccountByAccountNumber(transferCommand.sourceAccountNumber())).thenReturn(sourceAccount);
        when(customerLinkerPort.findCustomerIdByUserId(userId)).thenReturn(customerId);
        when(transactionOperationExecutor.updateAndSaveAccount(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        stubCreateTransactionCommand();
        stubTransactionHistoric();

        //-----processDeposit-----
        when(transactionOperationExecutor.getAccountByAccountNumber(transferCommand.targetAccountNumber())).thenReturn(targetAccount);
        when(transactionOperationExecutor.getExchangeRate("USD","MGA")).thenReturn(mgaExchangeRate);
        when(transactionOperationExecutor.convertToFinalAmount(any(BigDecimal.class), any(BigDecimal.class))).thenAnswer(i -> {
            BigDecimal originalAmount = i.getArgument(0);
            BigDecimal exchangeRate = i.getArgument(1);
            return new FinalAmount(originalAmount.multiply(exchangeRate));
        });

        //Then
        TransferResult result =  transferServiceApplication.execute(transferCommand);

        assertThat(result).isNotNull();
        Account sourceUpdated = result.sourceAccount();
        assertThat(sourceUpdated.getAccountNumber()).isEqualTo(new AccountNumber("010-20-1234567890"));
        assertThat(sourceUpdated.getBalance().value()).isEqualByComparingTo(new BigDecimal("40"));
        Account targetUpdated = result.targetAccount();
        assertThat(targetUpdated.getAccountNumber()).isEqualTo(new AccountNumber("010-10-1234567890"));
        assertThat(targetUpdated.getBalance().value()).isEqualByComparingTo(new BigDecimal("51000"));

        Transaction withdraw = result.withdrawTransaction();
        assertThat(withdraw.getAccountNumber()).isEqualTo(sourceAccount.getAccountNumber());
        assertThat(withdraw.getSoldBeforeTransaction().value()).isEqualByComparingTo(new BigDecimal("50"));
        assertThat(withdraw.getOriginalAmount().value()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(withdraw.getFinalAmount().value()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(withdraw.getTransactionCurrencyCode().value()).isEqualTo("USD");
        assertThat(withdraw.getTargetCurrencyCode().value()).isEqualTo("USD");
        assertThat(withdraw.getTransactionType().value()).isEqualTo(TransactionTypeEnum.WITHDRAWAL);

        Transaction deposit = result.depositTransaction();
        assertThat(deposit.getAccountNumber()).isEqualTo(targetAccount.getAccountNumber());
        assertThat(deposit.getSoldBeforeTransaction().value()).isEqualByComparingTo(new BigDecimal("1000"));
        assertThat(deposit.getOriginalAmount().value()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(deposit.getFinalAmount().value()).isEqualByComparingTo(new BigDecimal("50000"));
        assertThat(deposit.getTransactionCurrencyCode().value()).isEqualTo("USD");
        assertThat(deposit.getTargetCurrencyCode().value()).isEqualTo("MGA");
        assertThat(deposit.getTransactionType().value()).isEqualTo(TransactionTypeEnum.DEPOSIT);

        verify(transactionOperationExecutor, times(2)).getAccountByAccountNumber(any(AccountNumber.class));
        verify(transactionOperationExecutor).getExchangeRate(anyString(), anyString());
        verify(transactionOperationExecutor, times(2)).prepareTransactionCommand(
                any(), any(), any(), any(), any(), any(), any(), any()
        );
        verify(transactionOperationExecutor, times(2)).updateAndSaveAccount(any(Account.class));
        verify(createTransactionUseCase,times(2)).execute(any(CreateTransactionCommand.class));

        verify(transfertConfirmationPort).sendTransactionConfirmation(transfertCaptor.capture());
        TransfertConfirmationInfo info = transfertCaptor.getValue();

        assertThat(info.exchangeRate()).isEqualByComparingTo(new BigDecimal("5000"));
        assertThat(info.originalAmount()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(info.finalAmount()).isEqualByComparingTo(new BigDecimal("50000"));
        assertThat(info.userEmail().value()).isEqualTo("alibou@gmail.com");
        assertThat(info.username().value()).isEqualTo("alibou");
        assertThat(info.sourceAccountNumber()).isEqualTo(sourceAccount.getAccountNumber().value());
        assertThat(info.targetAccountNumber()).isEqualTo(targetAccount.getAccountNumber().value());
    }

    @Test
    @DisplayName("transfer-doit lever une exception si l'utilisateur connecté n'est pas la propriétaire du compte source ")
    void transfer_shouldThrowOperationNotPermittedException(){
        transferCommand = new TransferCommand(
                new AccountNumber("010-20-1234567890"),
                new AccountNumber("010-10-1234567890"),
                new OriginalAmount(new BigDecimal("10")),
                new Description("Test transfer"),
                userInfo
        );
        //-----processWithdraw-----
        CustomerId other = CustomerId.generate();
        when(transactionOperationExecutor.getAccountByAccountNumber(transferCommand.sourceAccountNumber())).thenReturn(sourceAccount);
        when(userInfo.userId()).thenReturn(userId);
        when(customerLinkerPort.findCustomerIdByUserId(any(UserId.class))).thenReturn(other);

        assertThatThrownBy(()-> transferServiceApplication.execute(transferCommand))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessageContaining("Transfert interrompue: le compte source ne vous appartient pas");
        verify(transactionOperationExecutor, never()).updateAndSaveAccount(any(Account.class));
        verify(createTransactionUseCase, never()).execute(any(CreateTransactionCommand.class));
    }

    @Test
    @DisplayName("transfer-doit lever une exception si le solde du source compte est insuffisant")
    void transfer_shouldThrowInsufficientBalanceException(){
        //-----processWithdraw-----
        transferCommand = new TransferCommand(
                new AccountNumber("010-20-1234567890"),
                new AccountNumber("010-10-1234567890"),
                new OriginalAmount(new BigDecimal("60")),
                new Description("Test transfer"),
                userInfo
        );

        when(transactionOperationExecutor.getAccountByAccountNumber(transferCommand.sourceAccountNumber())).thenReturn(sourceAccount);
        when(userInfo.userId()).thenReturn(userId);
        when(customerLinkerPort.findCustomerIdByUserId(any(UserId.class))).thenReturn(customerId);


        assertThatThrownBy(()-> transferServiceApplication.execute(transferCommand))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Solde insuffisant (limite de découvert dépassée).");
        verify(transactionOperationExecutor, never()).updateAndSaveAccount(any(Account.class));
        verify(createTransactionUseCase, never()).execute(any(CreateTransactionCommand.class));
    }
}
