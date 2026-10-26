package com.alibou.finance.accountType.infrastructure.transactional;

import com.alibou.finance.AbstractGithubProfileTest;
import com.alibou.finance.accountType.application.command.AccountTypeCommand;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.accountType.domain.vo.AccountFee;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.entity.AccountTypeEntity;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.repository.AccountTypeJpaRepository;
import com.alibou.finance.shared.domain.event.AccountTypeSavedEvent;
import com.alibou.finance.shared.domain.event.AnnualInterestRateChangeEvent;
import com.alibou.finance.shared.domain.vo.accountType.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.AssertablePublishedEvents;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;



/**
 * Pour ne charger que le module accountType et ses dépendances directes, ce qui est plus rapide que @SpringBootTest,
 * et plus conforme à la philosophie de test par module de l'architecture hexagonale.
 */
@ApplicationModuleTest
public class AccountTypeUseCaseProxyIT extends AbstractGithubProfileTest {
    @Autowired
    private AccountTypeUseCaseProxy service;

    @Autowired
    private AccountTypeJpaRepository repository;


    UUID createdBy = UUID.randomUUID();
    UUID accountTypeId = UUID.randomUUID();
    LocalDate now = LocalDate.now();

    AccountTypeEntity existing;

    @BeforeEach
    void setUp(){
        repository.deleteAll();
        // 1. Préparation : créer un compte existant avec un taux d'intérêt de 2.50
        existing = AccountTypeEntity.builder()
                .code("10")
                .id(accountTypeId)
                .createdBy(createdBy)
                .createdDate(now)
                .name("compte courant")
                .accountFee(new BigDecimal("5.00"))
                .annualInterestRate(new BigDecimal("2.50"))
                .minimumBalance(new BigDecimal("100.00"))
                .build();
        existing = repository.save(existing);
    }

    @Test
    void update_shouldPersistAndPublishBothEvents_whenRateChanged(AssertablePublishedEvents events) {
        // Construction de la commande de mise à jour : modifier le taux d'intérêt à 3.75
        AccountTypeCommand updateCmd = new AccountTypeCommand(
                new AccountTypeCode("10"),
                new AccountTypeName("Compte courant v2"),
                new AccountFee(new BigDecimal("7.00")),
                new InterestRate(new BigDecimal("3.75")),
                new MinimumBalance(new BigDecimal("150.00"))
        );

        // Exécution
        AccountType result = service.update(updateCmd, new AccountTypeId(accountTypeId));

        // Vérification de l'état de persistance
        AccountTypeEntity reloaded = repository.findById(accountTypeId).orElseThrow();
        assertThat(reloaded.getAnnualInterestRate()).isEqualByComparingTo(new BigDecimal("3.75"));

        // 5. Vérification des événements publiés
        assertThat(events)
                .contains(AccountTypeSavedEvent.class)
                .and()
                .contains(AnnualInterestRateChangeEvent.class);

        // Vérification supplémentaire des champs de l'événement
        assertThat(events.ofType(AnnualInterestRateChangeEvent.class))
                .singleElement()
                .satisfies(e -> assertThat(e.interestRate()).isEqualTo(new InterestRate(new BigDecimal("3.75"))))
                .satisfies(e -> assertThat(e.accountTypeId()).isEqualTo(new AccountTypeId(accountTypeId)));

        assertThat(events.ofType(AccountTypeSavedEvent.class))
                .singleElement()
                .satisfies(e -> assertThat(e.annualInterestRate()).isEqualTo(new InterestRate(new BigDecimal("3.75"))))
                .satisfies(e -> assertThat(e.accountTypeCode()).isEqualTo(new AccountTypeCode("10")))
                .satisfies(e -> assertThat(e.accountTypeId()).isEqualTo(new AccountTypeId(accountTypeId)));

    }

    @Test
    void update_shouldNotPublishEvent_whenAnnualInterestRateNoChanged(AssertablePublishedEvents events) {

        // Commande avec les mêmes valeurs qu'à la création (aucun changement)
        AccountTypeCommand cmd = new AccountTypeCommand(
                new AccountTypeCode("10"),
                new AccountTypeName("compte épargne"),
                new AccountFee(new BigDecimal("2.00")),
                new InterestRate(new BigDecimal("2.50")),//no changed
                new MinimumBalance(new BigDecimal("50.00"))
        );

        // Exécution
        service.update(cmd, new AccountTypeId(accountTypeId));

        // Vérification : aucun événement ne doit être publié
        assertThat(events.ofType(AccountTypeSavedEvent.class)).isEmpty();
        assertThat(events.ofType(AnnualInterestRateChangeEvent.class)).isEmpty();
    }

}
