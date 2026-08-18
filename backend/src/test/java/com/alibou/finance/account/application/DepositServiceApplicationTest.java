package com.alibou.finance.account.application;

import com.alibou.finance.account.application.port.dto.command.DepositCommand;
import com.alibou.finance.account.application.port.dto.output.TransactionResult;
import com.alibou.finance.account.application.service.DepositServiceApplication;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.exception.ThirdPartyServiceException;
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
public class DepositServiceApplicationTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CurrencyExchangePort currencyExchangePort;
    @Mock
    private ReferenceGenerator referenceGenerator;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private DepositServiceApplication depositService;

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
                .currency(usdCurrency)
                .accountStatus(AccountStatus.active())
                .mgaBalance(new MgaBalance(BigDecimal.valueOf(50000.00)))
                .balance(new Balance(BigDecimal.valueOf(10.00)))
                .overdraftLimit(new OverdraftLimit(BigDecimal.valueOf(2.0)))
                .customerId(new CustomerId(UUID.randomUUID()))
                .build();
        userConnected = User.builder().userId(UserId.generate()).username(new Username("alibou")).build();
    }

    @Test
    @DisplayName("Devrait effectuer l'opération du dépôts avec success")
    void shouldMakeDepositWithSuccess(){
        DepositCommand depositCommand = DepositCommand.builder()
                .accountNumber(account.getAccountNumber())
                .description(new Description("Simple description du dépôts"))
                .originalAmount(new OriginalAmount(BigDecimal.valueOf(50000)))
                .transactionCurrencyCode(new TransactionCurrencyCode("MGA"))
                .user(userConnected)
                .build();
        when(accountRepository.findByAccountNumber(depositCommand.accountNumber())).thenReturn(Optional.of(account));
        when(currencyExchangePort.getExchangeRate(depositCommand.transactionCurrencyCode().value(), "USD")).thenReturn(new BigDecimal("0.0002"));
        when(currencyExchangePort.getExchangeRate("USD", "MGA")).thenReturn(new BigDecimal("5000"));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(referenceGenerator.generateReferenceCharacter(any(Integer.class))).thenReturn("A12345");
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        TransactionResult result = depositService.execute(depositCommand);

        assertThat(result.account()).isNotNull();
        assertThat(result.account().getAccountNumber().value()).isEqualTo("001-10-1234567890");
        assertThat(result.account().getBalance().value().compareTo(BigDecimal.valueOf(0.0002 * 50000 + 10.00))).isEqualTo(0);
        assertThat(result.account().getMgaBalance().value().compareTo(
                BigDecimal.valueOf(0.0002 * 50000 + 10.00).multiply(BigDecimal.valueOf(5000)))
        ).isEqualTo(0);
        assertThat(result.transaction().getExchangeRate().value().compareTo(new BigDecimal("0.0002"))).isEqualTo(0);
        assertThat(result.transaction().getOriginalAmount().value().compareTo(new BigDecimal("50000"))).isEqualTo(0);
        assertThat(result.transaction().getFinalAmount().value().compareTo(
                new BigDecimal("50000").multiply(new BigDecimal("0.0002"))
        )).isEqualTo(0);
        assertThat((result.transaction().getReference().value())).isEqualTo("DEP-"+ now +"-A12345");

        verify(accountRepository, times(1)).findByAccountNumber(any(AccountNumber.class));
        verify(currencyExchangePort, times(2)).getExchangeRate(anyString(), anyString());
        verify(accountRepository).save(any(Account.class));
        verify(referenceGenerator).generateReferenceCharacter(6);
        verify(transactionRepository).save(any(Transaction.class));

    }

    @Test
    @DisplayName("Devrait lever une exception dans le cas ôu on a un problème de connexion par exemple")
    void deposit_shouldThrowThirdPartyServiceException(){
        DepositCommand depositCommand = DepositCommand.builder()
                .accountNumber(account.getAccountNumber())
                .description(new Description("Simple description du dépôts"))
                .originalAmount(new OriginalAmount(BigDecimal.valueOf(50000)))
                .transactionCurrencyCode(new TransactionCurrencyCode("MGA"))
                .user(userConnected)
                .build();

        when(accountRepository.findByAccountNumber(depositCommand.accountNumber())).thenReturn(Optional.of(account));
        when(currencyExchangePort.getExchangeRate(depositCommand.transactionCurrencyCode().value(), "USD"))
                .thenThrow(new ThirdPartyServiceException("Le service de conversion monétaire est temporairement indisponible. Veuillez réessayer plus tard."));

        assertThatThrownBy(
                ()-> depositService.execute(depositCommand)
        ).isInstanceOf(ThirdPartyServiceException.class)
                .hasMessage("Le service de conversion monétaire est temporairement indisponible. Veuillez réessayer plus tard.");


        verify(accountRepository, times(1)).findByAccountNumber(any(AccountNumber.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(referenceGenerator, never()).generateReferenceCharacter(6);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
