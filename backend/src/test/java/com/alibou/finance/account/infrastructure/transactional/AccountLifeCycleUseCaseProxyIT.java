package com.alibou.finance.account.infrastructure.transactional;


import com.alibou.finance.AbstractGithubProfileTest;
import com.alibou.finance.account.application.port.command.AccountLifeCycleCommand;
import com.alibou.finance.account.application.port.result.AccountLifeCycleResult;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.repository.AccountJpaRepository;
import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.history.infrastructure.adapter.out.persistence.entity.AccountStatusHistoryEntity;
import com.alibou.finance.history.infrastructure.adapter.out.persistence.repository.AccountStatusHistoryJpaRepository;
import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import com.alibou.finance.shared.domain.vo.account.AccountId;

import com.alibou.finance.shared.domain.vo.history.Reason;
import com.alibou.finance.shared.domain.vo.user.Email;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import java.time.Duration;

@ApplicationModuleTest(mode = ApplicationModuleTest.BootstrapMode.DIRECT_DEPENDENCIES)
@Execution(ExecutionMode.SAME_THREAD)
public class AccountLifeCycleUseCaseProxyIT extends AbstractGithubProfileTest {
    @Autowired
    private AccountJpaRepository accountJpaRepository;
    @Autowired
    private AccountStatusHistoryJpaRepository accountStatusHistoryJpaRepository;

    @Autowired
    private AccountLifeCycleUseCaseProxy accountLifeCycleService;

    AccountId accountId ;
    UserInfo userInfo;

    AccountLifeCycleCommand accountLifeCycleCommand;

    @BeforeEach
    void setUp(){
        accountStatusHistoryJpaRepository.deleteAll();
        accountJpaRepository.deleteAll();

        userInfo = new UserInfo(
                UserId.generate(),
                new Username("alibou"),
                new Email("alibou@gmail.com")
        );
        accountId = AccountId.generate();
        accountLifeCycleCommand = new AccountLifeCycleCommand(
                accountId,
                userInfo,
                new Reason("only for test")
        );
    }

    @Test
    @DisplayName("activateAccount - doit modifier le status en ACTIVE et verifier si le listener dans le module history a bien réagit au événement")
    void activateAccount_shouldActivateAccountAndPublishAccountStatusChangeEvent(){
        AccountEntity ae = create(new BigDecimal("10"), AccountStatusEnum.PENDING);
        accountJpaRepository.save(ae);

        //WHEN
        AccountLifeCycleResult result =  accountLifeCycleService.activateAccount(accountLifeCycleCommand);

        //verification de l'état de persistence
        AccountEntity updated = accountJpaRepository.findById(accountId.value()).orElseThrow();
        assertThat(updated.getAccountStatus()).isEqualTo(result.accountStatus().value());

        //Pour réexécuter de manière répétée l'ensemble du bloc d'assertions (lors d'une opération asynchrone),
        //jusqu'à ce que toutes les assertions réussissent, ou que le délai d'expiration soit atteint et qu'une exception soit levée.
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            List<AccountStatusHistoryEntity> accountStatusHistories = accountStatusHistoryJpaRepository.findAll();
            assertThat(accountStatusHistories).hasSize(1);
        });


    }

    @Test
    @DisplayName("closeAccount - doit fermer le compte  et verifier si le listener dans le module history a bien réagit au événement")
    void closeAccount_shouldCloseAccountAndPublishAccountStatusChangeEvent(){
        AccountEntity ae = create(BigDecimal.ZERO, AccountStatusEnum.ACTIVE);
        accountJpaRepository.save(ae);

        //WHEN
        accountLifeCycleService.closeAccount(accountLifeCycleCommand);

        // Vérification de l'état de persistance
        AccountEntity updated = accountJpaRepository.findById(ae.getId()).orElseThrow();
        assertThat(updated.getAccountStatus()).isEqualTo(AccountStatusEnum.CLOSED);

        // Duration.ofSeconds(5) : pour ne pas trop attendre s'il y a une erreur
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            List<AccountStatusHistoryEntity> accountStatusHistories = accountStatusHistoryJpaRepository.findAll();
            assertThat(accountStatusHistories.size()).isEqualTo(1);

            AccountStatusHistoryEntity accountStatusHistory = accountStatusHistories.stream().findFirst().orElseThrow();
            assertThat(accountStatusHistory.getDoingBy()).isEqualTo("alibou");
            assertThat(accountStatusHistory.getOldStatus()).isEqualTo(AccountStatusEnum.ACTIVE);
            assertThat(accountStatusHistory.getNewStatus()).isEqualTo(AccountStatusEnum.CLOSED);
        });
    }

    private AccountEntity create(BigDecimal balance, AccountStatusEnum status){
        return AccountEntity.builder()
                .mgaBalance(BigDecimal.ZERO)
                .overdraftLimit(BigDecimal.ZERO)
                .customerId(UUID.randomUUID())
                .annualInterestRate(new BigDecimal("3.65"))
                .balance(balance)
                .id(accountId.value())
                .accountStatus(status)
                .accountNumber("010-20-1234567890")
                .accountTypeName("compte épargne")
                .accountTypeCode("20")
                .accountTypeId(UUID.randomUUID())
                .currencyCode("MGA")
                .currencyId(UUID.randomUUID())
                .createdDate(LocalDate.now())
                .createdBy(UUID.randomUUID())
                .build();
    }
}
