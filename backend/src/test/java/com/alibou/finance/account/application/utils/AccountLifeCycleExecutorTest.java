package com.alibou.finance.account.application.utils;

import com.alibou.finance.account.application.port.command.AccountLifeCycleCommand;
import com.alibou.finance.account.application.port.result.AccountLifeCycleResult;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.out.event.AccountStatusEventPublisher;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.vo.account.AccountStatus;
import com.alibou.finance.account.domain.vo.account.Balance;
import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import com.alibou.finance.shared.domain.event.AccountStatusChangeEvent;
import com.alibou.finance.shared.domain.exception.IllegalOperationException;
import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.shared.domain.vo.history.Reason;
import com.alibou.finance.shared.domain.vo.user.Username;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountLifeCycleExecutorTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountStatusEventPublisher accountStatusEventPublisher;
    @InjectMocks
    private AccountLifeCycleExecutor accountLifeCycleExecutor;

    AccountLifeCycleCommand cmd ;
    Account account;
    AccountId accountId = AccountId.generate();
    UserInfo userInfo = mock(UserInfo.class);

    @Captor
    ArgumentCaptor<AccountStatusChangeEvent>statusCaptor;

    @BeforeEach
    void setUp(){
        cmd = new AccountLifeCycleCommand(
                accountId,
                userInfo,
                new Reason("test")
        );
    }

    @Test
    void executeTransition_to_active_account_successfully(){
       account = Account.builder()
                       .accountId(accountId)
                       .accountStatus(new AccountStatus(AccountStatusEnum.PENDING))
                       .build();
       when(accountRepository.findById(any(AccountId.class))).thenReturn(Optional.ofNullable(account));
       when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

       when(userInfo.username()).thenReturn(new Username("alibou"));

       AccountLifeCycleResult result =  accountLifeCycleExecutor.executeTransition(cmd, Account::active);

       assertThat(result.accountStatus().value()).isEqualTo(AccountStatusEnum.ACTIVE);
       verify(accountStatusEventPublisher).publish(statusCaptor.capture());
       AccountStatusChangeEvent event = statusCaptor.getValue();

       assertThat(event.accountId()).isEqualTo(accountId);
       assertThat(event.newStatus()).isEqualTo(AccountStatusEnum.ACTIVE);
       assertThat(event.oldStatus()).isEqualTo(AccountStatusEnum.PENDING);
    }

    @Test
    void executeTransition_to_reactive_account_successfully(){
        account = Account.builder()
                .accountId(accountId)
                .accountStatus(new AccountStatus(AccountStatusEnum.SUSPENDED))
                .build();
        when(accountRepository.findById(any(AccountId.class))).thenReturn(Optional.ofNullable(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        when(userInfo.username()).thenReturn(new Username("alibou"));

        AccountLifeCycleResult result =  accountLifeCycleExecutor.executeTransition(cmd, Account::reactive);

        assertThat(result.accountStatus().value()).isEqualTo(AccountStatusEnum.ACTIVE);
        verify(accountStatusEventPublisher).publish(statusCaptor.capture());
        AccountStatusChangeEvent event = statusCaptor.getValue();

        assertThat(event.accountId()).isEqualTo(accountId);
        assertThat(event.newStatus()).isEqualTo(AccountStatusEnum.ACTIVE);
        assertThat(event.oldStatus()).isEqualTo(AccountStatusEnum.SUSPENDED);
    }

    @Test
    @DisplayName("reactive_doit lever une exception lorsque le status du compte est deja closed")
    void reactive_shouldThrowIllegalOperationException(){
        account = Account.builder()
                .accountId(accountId)
                .accountStatus(new AccountStatus(AccountStatusEnum.CLOSED))
                .build();
        when(accountRepository.findById(any(AccountId.class))).thenReturn(Optional.ofNullable(account));

        assertThatThrownBy(
                () -> accountLifeCycleExecutor.executeTransition(cmd, Account::reactive)
        ).isInstanceOf(IllegalOperationException.class)
         .hasMessageContaining("Seul un compte suspendu peut être réactivé");

        verify(accountRepository,never()).save(any(Account.class));
        verify(accountStatusEventPublisher, never()).publish(any(AccountStatusChangeEvent.class));
    }

    @Test
    void executeTransition_to_suspend_account_successfully(){
        account = Account.builder()
                .accountId(accountId)
                .accountStatus(new AccountStatus(AccountStatusEnum.ACTIVE))
                .build();
        when(accountRepository.findById(any(AccountId.class))).thenReturn(Optional.ofNullable(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        when(userInfo.username()).thenReturn(new Username("alibou"));

        AccountLifeCycleResult result =  accountLifeCycleExecutor.executeTransition(cmd, Account::suspend);

        assertThat(result.accountStatus().value()).isEqualTo(AccountStatusEnum.SUSPENDED);
        verify(accountStatusEventPublisher).publish(statusCaptor.capture());
        AccountStatusChangeEvent event = statusCaptor.getValue();

        assertThat(event.accountId()).isEqualTo(accountId);
        assertThat(event.newStatus()).isEqualTo(AccountStatusEnum.SUSPENDED);
        assertThat(event.oldStatus()).isEqualTo(AccountStatusEnum.ACTIVE);
    }

    @Test
    void executeTransition_to_close_account_successfully(){
        account = Account.builder()
                .accountId(accountId)
                .accountStatus(new AccountStatus(AccountStatusEnum.SUSPENDED))
                .balance(new Balance(BigDecimal.ZERO))
                .build();
        when(accountRepository.findById(any(AccountId.class))).thenReturn(Optional.ofNullable(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        when(userInfo.username()).thenReturn(new Username("alibou"));

        AccountLifeCycleResult result =  accountLifeCycleExecutor.executeTransition(cmd, Account::close);

        assertThat(result.accountStatus().value()).isEqualTo(AccountStatusEnum.CLOSED);
        verify(accountStatusEventPublisher).publish(statusCaptor.capture());
        AccountStatusChangeEvent event = statusCaptor.getValue();

        assertThat(event.accountId()).isEqualTo(accountId);
        assertThat(event.newStatus()).isEqualTo(AccountStatusEnum.CLOSED);
        assertThat(event.oldStatus()).isEqualTo(AccountStatusEnum.SUSPENDED);
    }

    @Test
    void toClose_shouldThrowException_balance_not_nul(){
        account = Account.builder()
                .accountId(accountId)
                .accountStatus(new AccountStatus(AccountStatusEnum.SUSPENDED))
                .balance(new Balance(BigDecimal.ONE))
                .build();
        when(accountRepository.findById(any(AccountId.class))).thenReturn(Optional.ofNullable(account));
        assertThatThrownBy(
                () -> accountLifeCycleExecutor.executeTransition(cmd, Account::close)
        ).isInstanceOf(IllegalOperationException.class)
         .hasMessageContaining("Clôture impossible : le solde doit être nul");

        verify(accountRepository,never()).save(any(Account.class));
        verify(accountStatusEventPublisher, never()).publish(any(AccountStatusChangeEvent.class));
    }

}
