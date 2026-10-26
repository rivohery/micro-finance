package com.alibou.finance.accountType.application;

import com.alibou.finance.accountType.application.command.AccountTypeCommand;
import com.alibou.finance.accountType.application.service.AccountTypeServiceApplication;
import com.alibou.finance.accountType.application.utils.AccountTypeApplicationHelper;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.accountType.domain.out.event.AccountTypeEventPublisher;
import com.alibou.finance.accountType.domain.out.repository.AccountTypeRepository;
import com.alibou.finance.accountType.domain.vo.AccountFee;
import com.alibou.finance.shared.domain.event.AccountTypeSavedEvent;
import com.alibou.finance.shared.domain.event.AnnualInterestRateChangeEvent;
import com.alibou.finance.shared.domain.exception.DuplicateValueException;
import com.alibou.finance.shared.domain.vo.accountType.*;
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
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
public class AccountTypeServiceApplicationTest {
    @Mock
    private  AccountTypeRepository accountTypeRepository;
    @Mock
    private  AccountTypeApplicationHelper accountTypeApplicationHelper;

    @InjectMocks
    private AccountTypeServiceApplication accountTypeServiceApplication;

    @Captor
    private ArgumentCaptor<AccountType> accountTypeCaptor;


    private AccountTypeCommand validCommand;
    @BeforeEach
    void setUp() {
        validCommand = new AccountTypeCommand(
                new AccountTypeCode("10"),
                new AccountTypeName("Compte courant"),
                new AccountFee(new BigDecimal("5.00")),
                new InterestRate(new BigDecimal("2.50")),
                new MinimumBalance(new BigDecimal("100.00"))
        );
    }

    @Test
    @DisplayName("create() - doit créer un compte, le sauvegarder et publier AccountTypeSavedEvent")
    void create_shouldSaveAccountTypeAndPublishSavedEvent() {
        when(accountTypeRepository.save(any(AccountType.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AccountType created = accountTypeServiceApplication.create(validCommand);

        verify(accountTypeApplicationHelper).verifyUniquenessCode(any(AccountTypeCode.class));
        verify(accountTypeRepository).save(any(AccountType.class));
        verify(accountTypeApplicationHelper).publishAccountTypeSavedEvent(accountTypeCaptor.capture());

        AccountType captured = accountTypeCaptor.getValue();
        assertThat(created).isNotNull();
        assertThat(created.getAccountTypeId()).isEqualTo(captured.getAccountTypeId());
        assertThat(created.getCode()).isEqualTo(captured.getCode());
        assertThat(created.getAnnualInterestRate()).isEqualTo(captured.getAnnualInterestRate());

    }

    @Test
    @DisplayName("create() - doit propager DuplicateValueException si le code existe déjà")
    void create_shouldThrow_whenCodeAlreadyExists() {
        doThrow(new DuplicateValueException("Ce code est deja utilisé par un autre type"))
                .when(accountTypeApplicationHelper)
                .verifyUniquenessCode(any(AccountTypeCode.class));
        assertThatThrownBy(()-> accountTypeApplicationHelper.verifyUniquenessCode(validCommand.accountTypeCode()))
                .isInstanceOf(DuplicateValueException.class)
                .hasMessage("Ce code est deja utilisé par un autre type");

        verify(accountTypeRepository, never()).save(any(AccountType.class));
        verify(accountTypeApplicationHelper,never()).publishAccountTypeSavedEvent(any(AccountType.class));
    }

    @Test
    @DisplayName("create() - ne doit pas publier d'événement si le save retourne null")
    void create_shouldNotPublishEvent_whenSaveReturnsNull() {
        when(accountTypeRepository.save(any(AccountType.class)))
                .thenReturn(null);
        AccountType created = accountTypeServiceApplication.create(validCommand);

        assertThat(created).isNull();

        verify(accountTypeApplicationHelper).verifyUniquenessCode(any(AccountTypeCode.class));
        verify(accountTypeRepository).save(any(AccountType.class));
        verify(accountTypeApplicationHelper, never()).publishAccountTypeSavedEvent(any(AccountType.class));
    }

    @Test
    @DisplayName("update() - avec changement de taux : 2 événements publiés (rateChange puis saved)")
    void update_shouldPublishTwoEvents_whenRateChanged() {
        AccountTypeId accountTypeId = AccountTypeId.generate();
        AccountType existing = mock(AccountType.class);

        when(accountTypeApplicationHelper.getAccountTypeById(any(AccountTypeId.class))).thenReturn(existing);
        when(existing.updateFrom(any(AccountTypeCommand.class))).thenReturn(Set.of("annualInterestRate"));
        when(accountTypeRepository.save(any(AccountType.class))).thenAnswer(i -> i.getArgument(0));

        when(existing.getAccountTypeId()).thenReturn(accountTypeId);
        when(existing.getAnnualInterestRate()).thenReturn(validCommand.annualInterestRate());
        when(existing.getCode()).thenReturn(validCommand.accountTypeCode());


        AccountType updated = accountTypeServiceApplication.update(validCommand, accountTypeId);

        assertThat(updated).isEqualTo(existing);
        verify(accountTypeApplicationHelper).getAccountTypeById(any(AccountTypeId.class));
        verify(accountTypeRepository).save(any(AccountType.class));

        verify(accountTypeApplicationHelper).publishAccountTypeSavedEvent(accountTypeCaptor.capture());
        verify(accountTypeApplicationHelper).publishAnnualInterestRateChangeEvent(accountTypeCaptor.capture());

        AccountType captured = accountTypeCaptor.getValue();
        assertThat(captured.getAccountTypeId()).isEqualTo(accountTypeId);
        assertThat(captured.getAnnualInterestRate()).isEqualTo(validCommand.annualInterestRate());
        assertThat(captured.getCode()).isEqualTo(validCommand.accountTypeCode());

    }

    @Test
    @DisplayName("update() - ne doit publier aucun événement quand updatedField est vide")
    void update_shouldReturnActualAccountType() {
        AccountTypeId accountTypeId = AccountTypeId.generate();
        AccountType existing = mock(AccountType.class);

        when(accountTypeApplicationHelper.getAccountTypeById(any(AccountTypeId.class))).thenReturn(existing);
        when(existing.updateFrom(any(AccountTypeCommand.class))).thenReturn(Collections.emptySet());

        AccountType updated = accountTypeServiceApplication.update(validCommand, accountTypeId);
        assertThat(updated).isEqualTo(existing);

        verify(accountTypeRepository, never()).save(any(AccountType.class));
        verify(accountTypeApplicationHelper, never()).publishAccountTypeSavedEvent(any(AccountType.class));
        verify(accountTypeApplicationHelper, never()).publishAnnualInterestRateChangeEvent(any(AccountType.class));
    }

    @Test
    @DisplayName("update() - ne doit publier aucun événement car updatedField ne contient pas annualInterestRate")
    void update_shouldNotPublishAnyEvent() {
        AccountTypeId accountTypeId = AccountTypeId.generate();
        AccountType existing = mock(AccountType.class);

        when(accountTypeApplicationHelper.getAccountTypeById(any(AccountTypeId.class))).thenReturn(existing);
        when(existing.updateFrom(any(AccountTypeCommand.class))).thenReturn(Set.of("code", "minimumBalance"));
        when(accountTypeRepository.save(any(AccountType.class))).thenAnswer(i -> i.getArgument(0));

        AccountType updated = accountTypeServiceApplication.update(validCommand, accountTypeId);
        assertThat(updated).isEqualTo(existing);

        verify(accountTypeRepository).save(any(AccountType.class));
        verify(accountTypeApplicationHelper, never()).publishAccountTypeSavedEvent(any(AccountType.class));
        verify(accountTypeApplicationHelper, never()).publishAnnualInterestRateChangeEvent(any(AccountType.class));
    }
}
