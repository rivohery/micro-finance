package com.alibou.finance.account.infrastructure.out.repository;

import com.alibou.finance.BaseRepositoryIT;
import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.entity.AccountEntity;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.entity.AccountTypeEntity;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection.AccountProjection;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection.NumberAccountStatisticProj;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection.SoldeAccountStatisticProj;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.repository.AccountJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AccountJpaRepositoryIT extends BaseRepositoryIT {

    @Autowired
    private AccountJpaRepository accountRepository;

    UUID customerId = UUID.randomUUID();
    AccountTypeEntity checkingAccount = createAccountType(UUID.randomUUID(), "10", "compte courante");
    AccountTypeEntity savingAccount = createAccountType(UUID.randomUUID(), "20", "compte épargne");
    AccountTypeEntity businessAccount = createAccountType(UUID.randomUUID(), "30", "compte business");


    @BeforeEach
    void setUp(){
        accountRepository.deleteAll();

        // ==========================================
        // 1. ARRANGE (Préparation des données de test)
        // ==========================================

        LocalDate dailyDate = LocalDateTime.of(2026, 6, 1, 12, 0, 0).toLocalDate();



        accountRepository.saveAll(List.of(
                createAccount(checkingAccount, AccountStatusEnum.ACTIVE, "ACC-10-123456", BigDecimal.valueOf(200), dailyDate),
                createAccount(checkingAccount, AccountStatusEnum.CLOSED, "ACC-10-449967", BigDecimal.valueOf(600), dailyDate.minusDays(1)),
                createAccount(checkingAccount, AccountStatusEnum.ACTIVE, "ACC-10-443467", BigDecimal.valueOf(600), dailyDate.minusDays(1)),
                createAccount(checkingAccount, AccountStatusEnum.ACTIVE, "ACC-10-555456", BigDecimal.valueOf(300), dailyDate.plusDays(1)),
                createAccount(savingAccount, AccountStatusEnum.ACTIVE, "ACC-20-443467", BigDecimal.valueOf(6700), dailyDate),
                createAccount(savingAccount, AccountStatusEnum.ACTIVE, "ACC-20-123456", BigDecimal.valueOf(7000), dailyDate.plusDays(1)),
                createAccount(savingAccount, AccountStatusEnum.ACTIVE, "ACC-20-555456", BigDecimal.valueOf(4000), dailyDate.plusDays(2)),
                createAccount(businessAccount, AccountStatusEnum.CLOSED, "ACC-30-123456", BigDecimal.valueOf(200), dailyDate.plusDays(2)),
                createAccount(businessAccount, AccountStatusEnum.ACTIVE, "ACC-30-783456", BigDecimal.valueOf(200), dailyDate.plusDays(1)),
                createAccount(businessAccount, AccountStatusEnum.ACTIVE, "ACC-30-903457", BigDecimal.valueOf(200), dailyDate)
        ));

    }

    @Test
    void should_return_all_account_by_By_customerId(){
        List<AccountProjection> accountsProj = accountRepository.getAllByCustomerId(customerId);
        assertThat(accountsProj.size()).isEqualTo(10);
    }

    @Test
    @DisplayName("Devrait retourner les statistiques de comptes groupés par type en excluant les comptes fermés")
    void shouldGetStatisticNumberOfAccountNoClosed() {
        List<NumberAccountStatisticProj> statistics = accountRepository.getStatisticNumberOfAccountNoClosed();

        assertThat(statistics).isNotNull();
        assertThat(statistics).hasSize(3);

        NumberAccountStatisticProj savingsStats = statistics
                .stream()
                .filter(stat -> "compte épargne".equals(stat.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Le type 'Epargne' est manquant dans les statistiques"));
        assertThat(savingsStats.getNbrAccountByType()).isEqualTo(3L);
        assertThat(savingsStats.getAccountType()).isEqualTo("compte épargne");

        NumberAccountStatisticProj checkingsStats = statistics.stream()
                .filter(stat -> "compte courante".equals(stat.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Le type 'Courante' est manquant dans les statistiques"));
        assertThat(checkingsStats.getNbrAccountByType()).isEqualTo(3L);
        assertThat(checkingsStats.getAccountType()).isEqualTo("compte courante");

       NumberAccountStatisticProj businessStats = statistics.stream()
                .filter(stat -> "compte business".equals(stat.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Le type 'business' est manquant dans les statistiques"));
       assertThat(businessStats.getNbrAccountByType()).isEqualTo(2L);
       assertThat(businessStats.getAccountType()).isEqualTo("compte business");
    }


    @Test
    @DisplayName("Devrait retourner les statistiques des soldes de comptes groupés par type en excluant les comptes fermés")
    void shouldGetAccountStatisticSoldNoClosed() {
        List<SoldeAccountStatisticProj> statistics = accountRepository.getAccountStatisticSoldNoClosed();

        assertThat(statistics).isNotNull();
        assertThat(statistics).hasSize(3);

        SoldeAccountStatisticProj savingsStats = statistics.stream()
                .filter(stat -> "compte épargne".equals(stat.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Le type 'Epargne' est manquant dans les statistiques"));
        assertThat(savingsStats.getSoldeAccountByType().compareTo(BigDecimal.valueOf(17700))).isEqualTo(0);
        assertThat(savingsStats.getAccountType()).isEqualTo("compte épargne");

        SoldeAccountStatisticProj checkingsStats = statistics.stream()
                .filter(stat -> "compte courante".equals(stat.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Le type 'Courante' est manquant dans les statistiques"));
        assertThat(checkingsStats.getSoldeAccountByType().compareTo(BigDecimal.valueOf(1100))).isEqualTo(0);
        assertThat(checkingsStats.getAccountType()).isEqualTo("compte courante");

        SoldeAccountStatisticProj businessStats = statistics.stream()
                .filter(stat -> "compte business".equals(stat.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Le type 'business' est manquant dans les statistiques"));
        assertThat(businessStats.getSoldeAccountByType().compareTo(BigDecimal.valueOf(400))).isEqualTo(0);
        assertThat(businessStats.getAccountType()).isEqualTo("compte business");
    }

   @Test
   @DisplayName("Devrait retourner le total des soldes de comptes en excluant les comptes fermés")
    void shouldGetSoldTotalOfAccountNoClosed(){
        BigDecimal soldeTotal = accountRepository.getSoldTotalOfAccountNoClosed();
        assertThat(soldeTotal.compareTo(BigDecimal.valueOf(17700 + 1100 + 400))).isEqualTo(0);
    }

    @Test
    @DisplayName("Devrait retourner le nombres des comptes en excluant les comptes fermés")
    void shouldGetNbrTotalOfAccountNoClosed(){
        Long nbrAccount = accountRepository.getNbrTotalOfAccountNoClosed();
        assertThat(nbrAccount).isEqualTo(8);
    }

    @Test
    @DisplayName("Devrait tester la methode getAllAccountByAccountNumberBegin() avec succès")
    void shouldGetAllAccountByAccountNumberBeginSuccessfully(){
        Pageable pageable = PageRequest.of(0,4);
        String start = "ACC-10";
        Page<AccountProjection>response = accountRepository.getAllAccountByAccountNumberBegin(start, pageable);

        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getTotalElements()).isEqualTo(3);
        assertThat(response.getContent().size()).isEqualTo(3);
        assertThat(response.getContent())
                .extracting(AccountProjection::getAccountNumber).containsExactlyInAnyOrder("ACC-10-443467", "ACC-10-123456", "ACC-10-555456");
        assertThat(response.getContent()).extracting(AccountProjection::getCurrencyCode).containsExactly("MGA","MGA","MGA");
        assertThat(response.getContent()).extracting(AccountProjection::getAccountTypeName).containsOnly("compte courante");
    }

    @Test
    void should_getAccountsNoClosedByTypeId(){
        List<AccountEntity> accounts = accountRepository.getAccountsNoClosedByTypeId(businessAccount.getId());
        assertThat(accounts.size()).isEqualTo(2);
    }

    private AccountEntity createAccount(AccountTypeEntity accountType, AccountStatusEnum status, String accountNumber, BigDecimal mgaBalance, LocalDate createdDate) {
        return AccountEntity.builder()
                .mgaBalance(mgaBalance)
                .overdraftLimit(BigDecimal.ZERO)
                .customerId(customerId)
                .annualInterestRate(new BigDecimal("3.65"))
                .balance(mgaBalance)
                .id(UUID.randomUUID())
                .accountStatus(status)
                .accountNumber(accountNumber)
                .accountTypeName(accountType.getName())
                .accountTypeCode(accountType.getCode())
                .accountTypeId(accountType.getId())
                .currencyCode("MGA")
                .currencyId(UUID.randomUUID())
                .createdDate(createdDate)
                .createdBy(UUID.randomUUID())
                .build();
    }

    private AccountTypeEntity createAccountType(UUID id, String code, String name){
        return AccountTypeEntity.builder().id(id).code(code).name(name).build();
    }


}
