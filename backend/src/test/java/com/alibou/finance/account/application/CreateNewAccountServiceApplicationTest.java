package com.alibou.finance.account.application;

import com.alibou.finance.account.application.service.CreateNewAccountServiceApplication;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.service.AccountNumberGenerator;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.accountType.application.port.AccountTypeUseCase;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.accountType.domain.vo.*;
import com.alibou.finance.currency.application.port.CurrencyUseCase;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.currency.domain.vo.CurrencyName;
import com.alibou.finance.customer.application.port.CustomerLifeCycleUseCase;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.log.domain.vo.accountStatusHistory.InterestRate;
import com.alibou.finance.shared.domain.OperationNotPermittedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class CreateNewAccountServiceApplicationTest {

    @Mock
    private  CustomerLifeCycleUseCase customerLifeCycleUseCase;
    @Mock
    private  AccountTypeUseCase accountTypeUseCase;
    @Mock
    private  CurrencyUseCase currencyUseCase;
    @Mock
    private  AccountNumberGenerator accountNumberGenerator;
    @Mock
    private  CurrencyExchangePort currencyExchangePort;
    @Mock
    private  AccountRepository accountRepository;
    @InjectMocks
    private CreateNewAccountServiceApplication createNewAccountServiceApplication;

    Account account;

    @BeforeEach
    void setUp(){
        account = Account.builder()
                .accountType(new AccountType(new AccountTypeCode("10")))
                .currency(new Currency(new CurrencyCode("MGA")))
                .customerId(CustomerId.generate())
                .build();
    }

    @Test
    void create_ShouldReturnSavedAccount_WhenCustomerIsActive(){
        //Given
        when(customerLifeCycleUseCase.verifyIfCustomerIsActive(account.getCustomerId())).thenReturn(true);
        AccountType accountType = AccountType.builder()
                .accountTypeId(AccountTypeId.generate())
                .name(new AccountTypeName("epargne"))
                .code(account.getAccountType().getCode())
                .accountFee(new AccountFee(BigDecimal.ZERO))
                .annualInterestRate(InterestRate.setNull())
                .minimumBalance(new MinimumBalance(BigDecimal.valueOf(1000)))
                .build();
        Currency currency = Currency.builder()
                        .code(CurrencyCode.from("MGA"))
                        .name(new CurrencyName("Ariary"))
                        .build();
        when(accountTypeUseCase.findByCode(any(AccountTypeCode.class))).thenReturn(accountType);
        when(currencyUseCase.findByCode(any(CurrencyCode.class))).thenReturn(currency);
        when(accountNumberGenerator.generateUniqueAccountNumber("001","10",10))
                .thenReturn("001-10-1234567890");
        when(currencyExchangePort.getExchangeRate(anyString(), anyString())).thenReturn(BigDecimal.ONE);
        when(accountRepository.save(account)).thenAnswer(i -> i.getArgument(0));

        Account result = createNewAccountServiceApplication.execute(account);

        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber().value()).isEqualTo("001-10-1234567890");
        assertThat(result.getAccountType().getCode().value()).isEqualTo("10");
        assertThat(result.getCurrency().getCode().value()).isEqualTo("MGA");
        assertThat(result.getBalance().value().compareTo(BigDecimal.ZERO)).isEqualTo(0);
        assertThat(result.getMgaBalance().value().compareTo(BigDecimal.ZERO)).isEqualTo(0);
        assertThat(result.getAccountType().getName().value()).isEqualTo("EPARGNE");
        assertThat(result.getOverdraftLimit().value().compareTo(BigDecimal.valueOf(1000))).isEqualTo(0);

        verify(customerLifeCycleUseCase).verifyIfCustomerIsActive(any(CustomerId.class));
        verify(accountTypeUseCase).findByCode(any(AccountTypeCode.class));
        verify(currencyUseCase).findByCode(any(CurrencyCode.class));
        verify(currencyExchangePort).getExchangeRate(anyString(), anyString());
        verify(accountNumberGenerator).generateUniqueAccountNumber(anyString(), anyString(), any(Integer.class));
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void create_ShouldThrowException_WhenCustomerIsNotActive() {
        when(customerLifeCycleUseCase.verifyIfCustomerIsActive(any(CustomerId.class))).thenReturn(false);

        assertThatThrownBy(
                () -> createNewAccountServiceApplication.execute(account)
        ).isInstanceOf(OperationNotPermittedException.class)
                .hasMessage("Ce client n'est pas activé");

        verify(accountTypeUseCase, never()).findByCode(any(AccountTypeCode.class));
        verify(currencyUseCase, never()).findByCode(any(CurrencyCode.class));
        verify(currencyExchangePort, never()).getExchangeRate(anyString(), anyString());
        verify(accountNumberGenerator, never()).generateUniqueAccountNumber(anyString(), anyString(), any(Integer.class));
        verify(accountRepository, never()).save(any(Account.class));
    }
}
