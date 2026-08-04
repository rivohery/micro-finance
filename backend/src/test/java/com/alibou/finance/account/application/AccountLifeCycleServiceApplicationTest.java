package com.alibou.finance.account.application;

import com.alibou.finance.account.application.port.dto.input.AccountLifeCycleInput;
import com.alibou.finance.account.application.port.dto.vo.ChangedBy;
import com.alibou.finance.accountType.application.port.AccountTypeUseCase;
import com.alibou.finance.account.application.service.AccountLifeCycleServiceApplication;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.service.AccountNumberGenerator;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.vo.*;
import com.alibou.finance.accountType.domain.vo.*;
import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.auth.domain.vo.Username;
import com.alibou.finance.currency.application.port.CurrencyUseCase;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.currency.domain.vo.CurrencyName;
import com.alibou.finance.customer.application.port.CustomerLifeCycleUseCase;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.log.application.port.input.AccountStatusHistoryInput;
import com.alibou.finance.log.application.port.usecase.AccountStatusHistoryUseCase;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.log.domain.vo.accountStatusHistory.*;
import com.alibou.finance.shared.domain.IllegalOperationException;
import com.alibou.finance.shared.domain.OperationNotPermittedException;
import com.alibou.finance.account.domain.vo.AccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountLifeCycleServiceApplicationTest {

    @Mock
    private CustomerLifeCycleUseCase customerLifeCycleUseCase;


    @Mock
    private AccountTypeUseCase accountTypeUseCase;
    @Mock
    private AccountNumberGenerator accountNumberGenerator;
    @Mock
    private CurrencyUseCase currencyUseCase;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CurrencyExchangePort currencyExchangePort;
    @Mock
    private UserUseCase userUseCase;
    @Mock
    private AccountStatusHistoryUseCase accountStatusHistoryUseCase;

    @InjectMocks
    private AccountLifeCycleServiceApplication accountLifeCycleServiceApplication;

    Account account;
    User user;
    AccountLifeCycleInput accountLifeCycleInput;
    Currency currency;
    UUID customerId = UUID.randomUUID();

    @BeforeEach
    void setUp(){
        currency = Currency.builder().code(new CurrencyCode("MGA")).name(new CurrencyName("MGA Ariary")).build();
        accountLifeCycleInput =  AccountLifeCycleInput.builder()
                .accountId(new AccountId(UUID.randomUUID()))
                .changedBy(new ChangedBy(UUID.randomUUID()))
                .reason(new Reason("For test"))
                .build();
        account = Account.builder()
                .accountId(accountLifeCycleInput.accountId())
                .accountType(
                        new AccountType(new AccountTypeCode("10"))
                )
                .currency(new Currency(new CurrencyCode("10")))
                .accountNumber(new AccountNumber("117-20-0123456789"))
                .customerId(new CustomerId(customerId))
                .balance(new Balance(BigDecimal.ZERO))
                .build();
        user = User.builder()
                .enable(true)
                .username(new Username("alibou"))
                .userId(UserId.from(accountLifeCycleInput.changedBy().value()))
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
                        .minimumBalance(MinimumBalance.setNull())
                        .build();
        when(accountTypeUseCase.findByCode("10")).thenReturn(accountType);
        when(currencyUseCase.findByCode(any(CurrencyCode.class))).thenReturn(currency);
        when(accountNumberGenerator.generateUniqueAccountNumber("001","10",10))
                .thenReturn("001-10-1234567890");
        when(currencyExchangePort.getExchangeRate(anyString(), anyString())).thenReturn(BigDecimal.ONE);
        when(accountRepository.save(account)).thenAnswer(i -> i.getArgument(0));

        Account result = accountLifeCycleServiceApplication.create(account);

        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber().value()).isEqualTo("001-10-1234567890");
        assertThat(result.getAccountType().getCode().value()).isEqualTo("10");
        assertThat(result.getCurrency().getCode().value()).isEqualTo("MGA");
        assertThat(result.getBalance().value().compareTo(BigDecimal.ZERO)).isEqualTo(0);
        assertThat(result.getMgaBalance().value().compareTo(BigDecimal.ZERO)).isEqualTo(0);
        assertThat(result.getAccountType().getName().value()).isEqualTo("EPARGNE");
    }

    @Test
    void create_ShouldThrowException_WhenCustomerIsNotActive() {
        when(customerLifeCycleUseCase.verifyIfCustomerIsActive(any(CustomerId.class))).thenReturn(false);

        assertThatThrownBy(
                () -> accountLifeCycleServiceApplication.create(account)
        ).isInstanceOf(OperationNotPermittedException.class)
         .hasMessage("Création du compte interrompue car le client n'est pas active");

        verify(accountTypeUseCase, never()).findByCode(anyString());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void activateAccount_ShouldDoingWithSuccess(){
        //Given
        account.updateStatus(new AccountStatus(AccountStatusEnum.PENDING));
        AccountStatusHistory accountStatusHistory = AccountStatusHistory.builder()
                .accountId(account.getAccountId())
                .newStatus(new NewStatus(AccountStatusEnum.ACTIVE))
                .oldStatus(new OldStatus(AccountStatusEnum.PENDING))
                .accountStatusHistoryId(AccountStatusHistoryId.generate())
                .doingBy(new DoingBy(user.getUsername().value()))
                .build();

        when(userUseCase.findByUserId(any(UserId.class))).thenReturn(user);
        when(accountRepository.findById(any(AccountId.class))).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenAnswer(i -> i.getArgument(0));
        when(accountStatusHistoryUseCase.save(any(AccountStatusHistoryInput.class))).thenReturn(accountStatusHistory);

        Map<String, Object>result = accountLifeCycleServiceApplication.activateAccount(accountLifeCycleInput);

        assertThat(result.get("accountId")).isEqualTo(account.getAccountId());
        assertThat(result.get("accountHistoryId")).isEqualTo(accountStatusHistory.getAccountStatusHistoryId());
        assertThat(result.get("newStatus")).isEqualTo(AccountStatusEnum.ACTIVE);
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(accountStatusHistoryUseCase, times(1)).save(any(AccountStatusHistoryInput.class));
    }

    @Test
    void suspendAccount_shouldDoingWithSuccess(){
        //given
        account.updateStatus(new AccountStatus(AccountStatusEnum.ACTIVE));
        AccountStatusHistory accountStatusHistory = AccountStatusHistory.builder()
                .accountId(account.getAccountId())
                .newStatus(new NewStatus(AccountStatusEnum.SUSPENDED))
                .oldStatus(new OldStatus(account.getAccountStatus().value()))
                .accountStatusHistoryId(AccountStatusHistoryId.generate())
                .doingBy(new DoingBy(user.getUsername().value()))
                .build();

        when(userUseCase.findByUserId(new UserId(accountLifeCycleInput.changedBy().value()))).thenReturn(user);
        when(accountRepository.findById(accountLifeCycleInput.accountId())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenAnswer(i -> i.getArgument(0));
        when(accountStatusHistoryUseCase.save(any(AccountStatusHistoryInput.class))).thenReturn(accountStatusHistory);

        //When
        Map<String, Object>result = accountLifeCycleServiceApplication.suspendAccount(accountLifeCycleInput);
        //Then
        assertThat(result.get("accountId")).isEqualTo(account.getAccountId());
        assertThat(result.get("accountHistoryId")).isEqualTo(accountStatusHistory.getAccountStatusHistoryId());
        assertThat(result.get("newStatus")).isEqualTo(AccountStatusEnum.SUSPENDED);
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(accountStatusHistoryUseCase, times(1)).save(any(AccountStatusHistoryInput.class));
    }
    @Test
    void closeAccount_shouldCloseAccountSuccessufully(){
        account.updateStatus(new AccountStatus(AccountStatusEnum.ACTIVE));
        AccountStatusHistory accountStatusHistory = AccountStatusHistory.builder()
                .accountId(account.getAccountId())
                .newStatus(new NewStatus(AccountStatusEnum.CLOSED))
                .oldStatus(new OldStatus(account.getAccountStatus().value()))
                .accountStatusHistoryId(AccountStatusHistoryId.generate())
                .doingBy(new DoingBy(user.getUsername().value()))
                .build();

        when(userUseCase.findByUserId(any(UserId.class))).thenReturn(user);
        when(accountRepository.findById(account.getAccountId())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenAnswer(i -> i.getArgument(0));
        when(accountStatusHistoryUseCase.save(any(AccountStatusHistoryInput.class))).thenReturn(accountStatusHistory);

        Map<String, Object>result = accountLifeCycleServiceApplication.closeAccount(accountLifeCycleInput);

        assertThat(result.get("accountId")).isEqualTo(account.getAccountId());
        assertThat(result.get("accountHistoryId")).isEqualTo(accountStatusHistory.getAccountStatusHistoryId());
        assertThat(result.get("newStatus")).isEqualTo(AccountStatusEnum.CLOSED);
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(accountStatusHistoryUseCase, times(1)).save(any(AccountStatusHistoryInput.class));
    }

    @Test
    void closeAccount_shouldThrowIllegalOperationException(){
        //Given
        account.updateBalance(new Balance(BigDecimal.valueOf(1000)));
        account.updateStatus(new AccountStatus(AccountStatusEnum.ACTIVE));
        when(userUseCase.findByUserId(any(UserId.class))).thenReturn(user);
        when(accountRepository.findById(account.getAccountId())).thenReturn(Optional.of(account));

        assertThatThrownBy(
                ()-> accountLifeCycleServiceApplication.closeAccount(accountLifeCycleInput)
        ).isInstanceOf(IllegalOperationException.class)
         .hasMessage("On ne peut pas clôturer ce compte car le solde n'est pas nulle");

        verify(accountRepository, never()).save(any(Account.class));
        verify(accountStatusHistoryUseCase, never()).save(any(AccountStatusHistoryInput.class));
    }

}
