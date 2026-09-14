package com.alibou.finance.account.application;

import com.alibou.finance.account.application.port.dto.command.WithdrawCommand;
import com.alibou.finance.account.application.port.dto.output.TransactionResult;
import com.alibou.finance.account.application.service.WithdrawServiceApplication;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.out.service.ReferenceGenerator;
import com.alibou.finance.account.domain.vo.*;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.auth.domain.vo.Username;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.currency.domain.vo.CurrencyName;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.log.domain.vo.transaction.OriginalAmount;
import com.alibou.finance.log.domain.vo.transaction.TransactionCurrencyCode;
import com.alibou.finance.shared.domain.IllegalOperationException;
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
public class WithdrawServiceApplicationTest {
    @Mock
    private  AccountRepository accountRepository;
    @Mock
    private  CurrencyExchangePort currencyExchangePort;
    @Mock
    private  ReferenceGenerator referenceGenerator;
    @Mock
    private  TransactionRepository transactionRepository;
    @InjectMocks
    private WithdrawServiceApplication withdrawService;

    Account account;
    Currency usdCurrency;
    Currency mgaCurrency;
    User userConnected;

    final String now = LocalDate.now().toString().replace("-", "");

    @BeforeEach
    void setUp(){
        usdCurrency = Currency.builder().code(new CurrencyCode("USD")).name(new CurrencyName("Dollar USA")).build();
        mgaCurrency = Currency.builder().code(new CurrencyCode("MGA")).name(new CurrencyName("Ariary")).build();
        account = Account.builder()
                .accountNumber(new AccountNumber("001-10-1234567890"))
                .currency(mgaCurrency)
                .accountStatus(AccountStatus.active())
                .mgaBalance(new MgaBalance(BigDecimal.valueOf(100000.00)))
                .balance(new Balance(BigDecimal.valueOf(100000.00)))
                .overdraftLimit(new OverdraftLimit(BigDecimal.valueOf(2.0)))
                .customerId(new CustomerId(UUID.randomUUID()))
                .build();
        userConnected = User.builder().userId(UserId.generate()).username(new Username("alibou")).build();
    }

    @Test
    @DisplayName("Devrait effectuer l'opération du retrait avec success")
    void shouldWithdrawWithSuccess(){
        WithdrawCommand withdrawCommand = WithdrawCommand.builder()
                .accountNumber(new AccountNumber("001-10-1234567890"))
                .description(new Description("Simple test pour le retraits; monnaie du transaction different de celle du compte"))
                .user(userConnected)
                .originalAmount(new OriginalAmount(BigDecimal.valueOf(10)))
                .transactionCurrencyCode(new TransactionCurrencyCode("USD"))
                .build();


        when(accountRepository.findByAccountNumber(withdrawCommand.accountNumber())).thenReturn(Optional.of(account));
        when(currencyExchangePort.getExchangeRate(withdrawCommand.transactionCurrencyCode().value(), "MGA")).thenReturn(BigDecimal.valueOf(5000));
        when(currencyExchangePort.getExchangeRate(account.getCurrency().getCode().value(), "MGA")).thenReturn(BigDecimal.ONE);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(referenceGenerator.generateReferenceCharacter(6)).thenReturn("A12345");
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        TransactionResult result = withdrawService.execute(withdrawCommand);


        assertThat(result.account()).isNotNull();
        assertThat(result.account().getAccountNumber().value()).isEqualTo("001-10-1234567890");
        assertThat(result.account().getBalance().value().compareTo(BigDecimal.valueOf(100000.00 - (5000 * 10)))).isEqualTo(0);
        assertThat(result.account().getMgaBalance().value().compareTo(BigDecimal.valueOf(100000.00 - (5000 * 10)))).isEqualTo(0);

        assertThat(result.transaction()).isNotNull();
        assertThat(result.transaction().getExchangeRate().value().compareTo(BigDecimal.valueOf(5000))).isEqualTo(0);
        assertThat(result.transaction().getOriginalAmount().value().compareTo(BigDecimal.valueOf(10))).isEqualTo(0);
        assertThat(result.transaction().getFinalAmount().value().compareTo(BigDecimal.valueOf(5000 * 10))).isEqualTo(0);
        assertThat(result.transaction().getAccountNumber().value()).isEqualTo("001-10-1234567890");
        assertThat(result.transaction().getReference().value()).isEqualTo("WIT-" + now + "-A12345");
        assertThat(result.transaction().getDescription().value()).isEqualTo("Simple test pour le retraits; monnaie du transaction different de celle du compte");

        verify(accountRepository, times(1)).findByAccountNumber(any(AccountNumber.class));
        verify(currencyExchangePort, times(2)).getExchangeRate(anyString(), anyString());
        verify(accountRepository).save(any(Account.class));
        verify(referenceGenerator).generateReferenceCharacter(6);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Devrait lever une exception dans le cas ôu le solde est insuffisant")
    void withdraw_shouldThrowIllegalOperationException(){
        WithdrawCommand withdrawCommand = WithdrawCommand.builder()
                .accountNumber(new AccountNumber("001-10-1234567890"))
                .description(new Description("Simple test pour le retraits; monnaie du transaction different de celle du compte"))
                .user(User.builder().username(new Username("john")).build())
                .originalAmount(new OriginalAmount(BigDecimal.valueOf(5000)))
                .transactionCurrencyCode(new TransactionCurrencyCode("USD"))
                .build();


        when(accountRepository.findByAccountNumber(withdrawCommand.accountNumber())).thenReturn(Optional.of(account));
        when(currencyExchangePort.getExchangeRate(withdrawCommand.transactionCurrencyCode().value(), "MGA")).thenReturn(BigDecimal.valueOf(5000));

        assertThatThrownBy(
                () -> withdrawService.execute(withdrawCommand)
        ).isInstanceOf(IllegalOperationException.class)
                .hasMessage("Solde du compte insuffisant : " + account.getBalance().value().doubleValue());


        verify(accountRepository, times(1)).findByAccountNumber(any(AccountNumber.class));
        verify(currencyExchangePort, times(1)).getExchangeRate("USD", "MGA");
        verify(accountRepository, never()).save(any(Account.class));
        verify(referenceGenerator, never()).generateReferenceCharacter(6);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
