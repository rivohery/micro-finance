package com.alibou.finance.account.application;

import com.alibou.finance.account.application.port.dto.command.TransferCommand;
import com.alibou.finance.account.application.port.dto.output.TransferResult;
import com.alibou.finance.account.application.service.TransferServiceApplication;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.out.service.ReferenceGenerator;
import com.alibou.finance.account.domain.out.service.TransfertConfirmationPort;
import com.alibou.finance.account.domain.out.service.dto.TransfertConfirmationInfo;
import com.alibou.finance.account.domain.vo.*;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.auth.domain.vo.Username;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.currency.domain.vo.CurrencyName;
import com.alibou.finance.customer.application.port.CustomerConsultationUseCase;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.log.domain.vo.transaction.OriginalAmount;
import com.alibou.finance.shared.domain.OperationNotPermittedException;
import com.alibou.finance.shared.vo.domain.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class TransferServiceApplicationTest {
    @Mock
    private  TransactionRepository transactionRepository;
    @Mock
    private  AccountRepository accountRepository;
    @Mock
    private  CurrencyExchangePort currencyExchangePort;
    @Mock
    private  CustomerConsultationUseCase customerService;
    @Mock
    private  ReferenceGenerator referenceGenerator;
    @Mock
    private  TransfertConfirmationPort transfertConfirmationService;
    @InjectMocks
    private TransferServiceApplication transferServiceApplication;

    Currency usdCurrency;
    Currency mgaCurrency;
    User userConnected;
    final String now = LocalDate.now().toString().replace("-", "");

    @BeforeEach
    void setUp(){
        usdCurrency = Currency.builder().code(new CurrencyCode("USD")).name(new CurrencyName("Dollar USA")).build();
        mgaCurrency = Currency.builder().code(new CurrencyCode("MGA")).name(new CurrencyName("Ariary")).build();
        userConnected = User.builder().userId(UserId.generate()).username(new Username("alibou")).build();
    }

    @Test
    void shouldTransferWithSuccess(){
        Account sourceAccount = Account.builder()//=>USD
                .accountNumber(new AccountNumber("001-10-1234567890"))
                .currency(usdCurrency)
                .accountStatus(AccountStatus.active())
                .mgaBalance(new MgaBalance(BigDecimal.valueOf(500000.00)))
                .balance(new Balance(BigDecimal.valueOf(100.00)))
                .overdraftLimit(new OverdraftLimit(BigDecimal.valueOf(2.0)))
                .customerId(new CustomerId(UUID.randomUUID()))
                .build();
        Account targetAccount = Account.builder()//=>MGA
                .accountNumber(new AccountNumber("001-20-1234567896"))
                .currency(mgaCurrency)
                .accountStatus(AccountStatus.active())
                .mgaBalance(new MgaBalance(BigDecimal.valueOf(10000.00)))
                .balance(new Balance(BigDecimal.valueOf(10000.00)))
                .overdraftLimit(new OverdraftLimit(BigDecimal.valueOf(1.0)))
                .build();


        TransferCommand transfertCommand = TransferCommand
                .builder()
                .sourceAccountNumber(new AccountNumber("001-10-1234567890"))
                .targetAccountNumber(new AccountNumber("001-20-1234567896"))
                .description(new Description("Test pour le transfert"))
                .user(userConnected)
                .originalAmount(new OriginalAmount(BigDecimal.valueOf(10.0)))
                .build();

        //withdraw operation
        when(accountRepository.findByAccountNumber(transfertCommand.sourceAccountNumber())).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber(transfertCommand.targetAccountNumber())).thenReturn(Optional.of(targetAccount));
        when(customerService.findCustomerIdByUser(any(User.class))).thenReturn(sourceAccount.getCustomerId());
        //common operation
        when(currencyExchangePort.getExchangeRate("USD","MGA")).thenReturn(BigDecimal.valueOf(5000));
        //ArgumentCaptor<Account> sourceAccountCaptor = ArgumentCaptor.forClass(Account.class);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(referenceGenerator.generateReferenceCharacter(6)).thenReturn("A12345");
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
        //deposit operation
        when(currencyExchangePort.getExchangeRate("MGA", "MGA")).thenReturn(BigDecimal.ONE);

        //final
        when(transfertConfirmationService.sendTransactionConfirmation(any(TransfertConfirmationInfo.class))).thenReturn(1);

        TransferResult result = transferServiceApplication.execute(transfertCommand);

        //--------withdraw operation-----
        Account sourceAccountResp = result.sourceAccount();
        assertThat(sourceAccountResp).isNotNull();
        assertThat(sourceAccountResp.getAccountNumber().value()).isEqualTo("001-10-1234567890");
        assertThat(sourceAccountResp.getBalance().value().compareTo(BigDecimal.valueOf(100.00 - 10.0))).isEqualTo(0);
        assertThat(sourceAccountResp.getMgaBalance().value().compareTo(BigDecimal.valueOf(90.00 * 5000.00))).isEqualTo(0);
        //-------
        Transaction withdrawTransaction = result.withdrawTransaction();
        assertThat(withdrawTransaction).isNotNull();
        assertThat(withdrawTransaction.getExchangeRate().value().compareTo(BigDecimal.ONE)).isEqualTo(0);
        assertThat(withdrawTransaction.getOriginalAmount().value().compareTo(BigDecimal.valueOf(10.00))).isEqualTo(0);
        assertThat(withdrawTransaction.getFinalAmount().value().compareTo(BigDecimal.valueOf(10.00))).isEqualTo(0);
        assertThat(withdrawTransaction.getAccountNumber().value()).isEqualTo("001-10-1234567890");
        assertThat(withdrawTransaction.getReference().value()).isEqualTo("WIT-"+ now +"-A12345");
        assertThat(withdrawTransaction.getDescription().value()).isEqualTo("Test pour le transfert");

        //--------deposit operation-------
        Account targetAccountResp = result.targetAccount();
        assertThat(targetAccountResp).isNotNull();
        assertThat(targetAccountResp.getAccountNumber().value()).isEqualTo("001-20-1234567896");
        assertThat(targetAccountResp.getBalance().value().compareTo(BigDecimal.valueOf(10000.00 + (10.0 * 5000)))).isEqualTo(0);
        assertThat(targetAccountResp.getMgaBalance().value().compareTo(BigDecimal.valueOf((10000.00 + (10.0 * 5000)) * 1))).isEqualTo(0);
        //-------
        Transaction depositTransaction = result.depositTransaction();
        assertThat(depositTransaction).isNotNull();
        assertThat(depositTransaction.getExchangeRate().value().compareTo(BigDecimal.valueOf(5000))).isEqualTo(0);
        assertThat(depositTransaction.getOriginalAmount().value().compareTo(BigDecimal.valueOf(10.00))).isEqualTo(0);
        assertThat(depositTransaction.getFinalAmount().value().compareTo(BigDecimal.valueOf(10.00 * 5000))).isEqualTo(0);
        assertThat(depositTransaction.getAccountNumber().value()).isEqualTo("001-20-1234567896");
        assertThat(depositTransaction.getReference().value()).isEqualTo("DEP-"+ now +"-A12345");
        assertThat(depositTransaction.getDescription().value()).isEqualTo("Test pour le transfert");

        verify(accountRepository, times(2)).findByAccountNumber(any(AccountNumber.class));
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(currencyExchangePort, times(3)).getExchangeRate(anyString(), anyString());
        verify(referenceGenerator, times(2)).generateReferenceCharacter(6);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(transfertConfirmationService).sendTransactionConfirmation(any(TransfertConfirmationInfo.class));
    }

    @Test
    @DisplayName("Doit lever OperationNotPermittedException lorsque le compte source n'appartient pas à l'utilisateur connecté")
    void shouldThrowOperationNotPermittedException(){
        Account sourceAccount = Account.builder()//=>USD
                .accountNumber(new AccountNumber("001-10-1234567890"))
                .currency(usdCurrency)
                .accountStatus(AccountStatus.active())
                .mgaBalance(new MgaBalance(BigDecimal.valueOf(500000.00)))
                .balance(new Balance(BigDecimal.valueOf(100.00)))
                .overdraftLimit(new OverdraftLimit(BigDecimal.valueOf(2.0)))
                .customerId(new CustomerId(UUID.randomUUID()))
                .build();
        TransferCommand transfertCommand = TransferCommand
                .builder()
                .sourceAccountNumber(new AccountNumber("001-10-1234567890"))
                .targetAccountNumber(new AccountNumber("001-20-1234567896"))
                .description(new Description("Test pour le transfert"))
                .user(userConnected)
                .originalAmount(new OriginalAmount(BigDecimal.valueOf(10.0)))
                .build();
        when(accountRepository.findByAccountNumber(transfertCommand.sourceAccountNumber())).thenReturn(Optional.of(sourceAccount));
        when(customerService.findCustomerIdByUser(any(User.class))).thenReturn(new CustomerId(UUID.fromString("00000000-0000-0000-0000-000000000000".replace("0","9"))));

        assertThatThrownBy(() -> transferServiceApplication.execute(transfertCommand))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessage("Transfert interrompue: le compte source ne vous appartient pas");

        verify(accountRepository).findByAccountNumber(any(AccountNumber.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(currencyExchangePort, never()).getExchangeRate(anyString(), anyString());
        verify(referenceGenerator, never()).generateReferenceCharacter(6);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(transfertConfirmationService, never()).sendTransactionConfirmation(any(TransfertConfirmationInfo.class));


    }
}
