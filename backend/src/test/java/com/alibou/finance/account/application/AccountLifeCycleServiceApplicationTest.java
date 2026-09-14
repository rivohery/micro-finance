package com.alibou.finance.account.application;

import com.alibou.finance.account.application.port.dto.command.AccountLifeCycleCommand;
import com.alibou.finance.account.application.port.dto.output.AccountLifeCycleResult;
import com.alibou.finance.account.application.port.dto.vo.ChangedBy;
import com.alibou.finance.account.application.service.AccountLifeCycleServiceApplication;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.vo.*;
import com.alibou.finance.accountType.domain.vo.*;
import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.auth.domain.vo.Username;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.currency.domain.vo.CurrencyName;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.log.application.port.usecase.AccountStatusHistoryUseCase;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.log.domain.vo.accountStatusHistory.*;
import com.alibou.finance.shared.domain.IllegalOperationException;
import com.alibou.finance.account.domain.vo.AccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountLifeCycleServiceApplicationTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserUseCase userUseCase;
    @Mock
    private AccountStatusHistoryUseCase accountStatusHistoryUseCase;
    @InjectMocks
    private AccountLifeCycleServiceApplication accountLifeCycleServiceApplication;

    Account account;
    User user;
    AccountLifeCycleCommand accountLifeCycleCommand;
    Currency currency;
    UUID customerId = UUID.randomUUID();
    ChangedBy changedBy = new ChangedBy(UUID.randomUUID());

    @BeforeEach
    void setUp(){
        currency = Currency.builder().code(new CurrencyCode("MGA")).name(new CurrencyName("MGA Ariary")).build();
        accountLifeCycleCommand =  AccountLifeCycleCommand.builder()
                .accountId(new AccountId(UUID.randomUUID()))
                .changedBy(changedBy)
                .reason(new Reason("For test only"))
                .build();
        account = Account.builder()
                .accountId(accountLifeCycleCommand.accountId())
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
                .userId(new UserId(changedBy.value()))
                .build();

    }


    @Test
    void activateAccount_ShouldDoingWithSuccess(){
        //Given
        account.updateStatus(new AccountStatus(AccountStatusEnum.PENDING));

        when(userUseCase.findByUserId(any(UserId.class))).thenReturn(user);
        when(accountRepository.findById(any(AccountId.class))).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenAnswer(i -> i.getArgument(0));
        when(accountStatusHistoryUseCase.save(any(AccountStatusHistory.class))).thenAnswer(i -> i.getArgument(0));

        AccountLifeCycleResult result = accountLifeCycleServiceApplication.activateAccount(accountLifeCycleCommand);

        assertThat(result.account().getAccountId().value()).isEqualTo(account.getAccountId().value());
        assertThat(result.account().getAccountStatus().value()).isEqualTo(AccountStatusEnum.ACTIVE);

        assertThat(result.history().getAccountStatusHistoryId()).isNotNull();
        assertThat(result.history().getOldStatus().value()).isEqualTo(AccountStatusEnum.PENDING);
        assertThat(result.history().getNewStatus().value()).isEqualTo(AccountStatusEnum.ACTIVE);
        assertThat(result.history().getDoingBy().value()).isEqualTo("alibou");

        verify(userUseCase).findByUserId(any(UserId.class));
        verify(accountRepository).findById(any(AccountId.class));
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(accountStatusHistoryUseCase, times(1)).save(any(AccountStatusHistory.class));
    }

    @Test
    void suspendAccount_shouldDoingWithSuccess(){
        //given
        account.updateStatus(new AccountStatus(AccountStatusEnum.ACTIVE));

        when(userUseCase.findByUserId(any(UserId.class))).thenReturn(user);
        when(accountRepository.findById(accountLifeCycleCommand.accountId())).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(accountStatusHistoryUseCase.save(any(AccountStatusHistory.class))).thenAnswer(i -> i.getArgument(0));

        //When
        AccountLifeCycleResult result = accountLifeCycleServiceApplication.suspendAccount(accountLifeCycleCommand);
        //Then
        assertThat(result.account().getAccountId().value()).isEqualTo(account.getAccountId().value());
        assertThat(result.account().getAccountStatus().value()).isEqualTo(AccountStatusEnum.SUSPENDED);

        assertThat(result.history().getAccountStatusHistoryId()).isNotNull();
        assertThat(result.history().getOldStatus().value()).isEqualTo(AccountStatusEnum.ACTIVE);
        assertThat(result.history().getNewStatus().value()).isEqualTo(AccountStatusEnum.SUSPENDED);
        assertThat(result.history().getDoingBy().value()).isEqualTo("alibou");

        verify(userUseCase).findByUserId(any(UserId.class));
        verify(accountRepository).findById(any(AccountId.class));
        verify(accountRepository).save(any(Account.class));
        verify(accountStatusHistoryUseCase).save(any(AccountStatusHistory.class));
    }

    @Test
    void closeAccount_shouldCloseAccountSuccessufully(){
        account.updateStatus(new AccountStatus(AccountStatusEnum.SUSPENDED));

        when(userUseCase.findByUserId(any(UserId.class))).thenReturn(user);
        when(accountRepository.findById(any(AccountId.class))).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(accountStatusHistoryUseCase.save(any(AccountStatusHistory.class))).thenAnswer(i -> i.getArgument(0));


        AccountLifeCycleResult result = accountLifeCycleServiceApplication.closeAccount(accountLifeCycleCommand);

        assertThat(result.account().getAccountId().value()).isEqualTo(account.getAccountId().value());
        assertThat(result.account().getAccountStatus().value()).isEqualTo(AccountStatusEnum.CLOSED);

        assertThat(result.history().getAccountStatusHistoryId()).isNotNull();
        assertThat(result.history().getOldStatus().value()).isEqualTo(AccountStatusEnum.SUSPENDED);
        assertThat(result.history().getNewStatus().value()).isEqualTo(AccountStatusEnum.CLOSED);
        assertThat(result.history().getDoingBy().value()).isEqualTo("alibou");

        verify(userUseCase).findByUserId(any(UserId.class));
        verify(accountRepository).findById(any(AccountId.class));
        verify(accountRepository).save(any(Account.class));
        verify(accountStatusHistoryUseCase).save(any(AccountStatusHistory.class));
    }

    @Test
    void closeAccount_shouldThrowIllegalOperationException(){
        //Given
        account.updateBalance(new Balance(BigDecimal.valueOf(1000)));
        account.updateStatus(new AccountStatus(AccountStatusEnum.ACTIVE));

        when(userUseCase.findByUserId(any(UserId.class))).thenReturn(user);
        when(accountRepository.findById(any(AccountId.class))).thenReturn(Optional.of(account));

        assertThatThrownBy(
                ()-> accountLifeCycleServiceApplication.closeAccount(accountLifeCycleCommand)
        ).isInstanceOf(IllegalOperationException.class)
         .hasMessage("Clôture interrompu: le solde du compte n'est pas nulle");

        verify(accountRepository, never()).save(any(Account.class));
        verify(accountStatusHistoryUseCase, never()).save(any(AccountStatusHistory.class));
    }

}
