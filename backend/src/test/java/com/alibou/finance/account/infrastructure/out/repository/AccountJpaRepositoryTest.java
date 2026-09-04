package com.alibou.finance.account.infrastructure.out.repository;

import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.entity.AccountTypeEntity;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.projection.AccountProjection;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.projection.NumberAccountStatisticProj;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.projection.SoldeAccountStatisticProj;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.repository.AccountJpaRepository;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.repository.AccountTypeJpaRepository;
import com.alibou.finance.currency.infrastructure.adapter.out.entity.CurrencyEntity;
import com.alibou.finance.currency.infrastructure.adapter.out.repository.CurrencyJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
public class AccountJpaRepositoryTest {
    @Autowired
    private AccountJpaRepository accountRepository;
    @Autowired
    private AccountTypeJpaRepository accountTypeRepository;
    @Autowired
    private CurrencyJpaRepository currencyRepository;

    UUID customerId = UUID.randomUUID();
    CurrencyEntity mgaCurrencyEntity;

    AccountTypeEntity checkingAccount;
    AccountTypeEntity savingAccount;
    AccountTypeEntity businessAccount;


    @BeforeEach
    void setUp(){
        currencyRepository.deleteAll();
        accountTypeRepository.deleteAll();
        accountRepository.deleteAll();

        // ==========================================
        // 1. ARRANGE (Préparation des données de test)
        // ==========================================
        mgaCurrencyEntity = CurrencyEntity.builder()
                .code("MGA")
                .enable(true)
                .id(UUID.randomUUID())
                .name("Ariary")
                .build();
        mgaCurrencyEntity = currencyRepository.save(mgaCurrencyEntity);

        checkingAccount = accountTypeRepository.save(createAccountType("10", "Compte courante"));
        savingAccount = accountTypeRepository.save(createAccountType("20", "Compte épargne"));
        businessAccount = accountTypeRepository.save(createAccountType("30", "Compte business"));

        accountRepository.saveAll(List.of(
                createAccount(checkingAccount, AccountStatusEnum.ACTIVE, "ACC-10-123456", BigDecimal.valueOf(200), LocalDate.now()),
                createAccount(checkingAccount, AccountStatusEnum.ACTIVE, "ACC-10-443467", BigDecimal.valueOf(600), LocalDate.now().minusDays(1)),
                createAccount(checkingAccount, AccountStatusEnum.ACTIVE, "ACC-10-555456", BigDecimal.valueOf(300), LocalDate.now().plusDays(1)),
                createAccount(savingAccount, AccountStatusEnum.ACTIVE, "ACC-20-443467", BigDecimal.valueOf(6700), LocalDate.now()),
                createAccount(savingAccount, AccountStatusEnum.ACTIVE, "ACC-20-123456", BigDecimal.valueOf(7000), LocalDate.now().plusDays(1)),
                createAccount(savingAccount, AccountStatusEnum.ACTIVE, "ACC-20-555456", BigDecimal.valueOf(4000), LocalDate.now().plusDays(2)),
                createAccount(businessAccount, AccountStatusEnum.CLOSED, "ACC-30-123456", BigDecimal.valueOf(200), LocalDate.now().plusDays(2)),
                createAccount(businessAccount, AccountStatusEnum.ACTIVE, "ACC-30-783456", BigDecimal.valueOf(200), LocalDate.now().plusDays(1)),
                createAccount(businessAccount, AccountStatusEnum.ACTIVE, "ACC-30-903457", BigDecimal.valueOf(200), LocalDate.now())
        ));

    }

    @Test
    @DisplayName("Devrait retourner les statistiques de comptes groupés par type en excluant les comptes fermés")
    void shouldGetStatisticNumberOfAccountNoClosed() {
        List<NumberAccountStatisticProj> statistics = accountRepository.getStatisticNumberOfAccountNoClosed(AccountStatusEnum.CLOSED);

        assertThat(statistics).isNotNull();
        assertThat(statistics).hasSize(3);

        NumberAccountStatisticProj savingsStats = statistics.stream()
                .filter(stat -> "Compte épargne".equals(stat.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Le type 'Epargne' est manquant dans les statistiques"));
        assertThat(savingsStats.getNbrAccountByType()).isEqualTo(3L);
        assertThat(savingsStats.getAccountType()).isEqualTo("Compte épargne");

        NumberAccountStatisticProj checkingsStats = statistics.stream()
                .filter(stat -> "Compte courante".equals(stat.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Le type 'Courante' est manquant dans les statistiques"));
        assertThat(checkingsStats.getNbrAccountByType()).isEqualTo(3L);
        assertThat(checkingsStats.getAccountType()).isEqualTo("Compte courante");

       NumberAccountStatisticProj businessStats = statistics.stream()
                .filter(stat -> "Compte business".equals(stat.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Le type 'business' est manquant dans les statistiques"));
       assertThat(businessStats.getNbrAccountByType()).isEqualTo(2L);
       assertThat(businessStats.getAccountType()).isEqualTo("Compte business");
    }

    @Test
    @DisplayName("Devrait retourner les statistiques de comptes groupés par type en excluant les comptes fermés et pas de compte business")
    void shouldGetStatisticNumberOfAccountNoClosedAndOneTypeNoPresent() {
        //No Business account
       accountRepository.deleteAll(
               accountRepository.findAll().stream().filter(cu -> cu.getAccountTypeEntity().getName().equals("Compte business")).toList()
       );

        List<NumberAccountStatisticProj> statistics = accountRepository.getStatisticNumberOfAccountNoClosed(AccountStatusEnum.CLOSED);

        assertThat(statistics).isNotNull();
        assertThat(statistics).hasSize(2);

        NumberAccountStatisticProj savingsStats = statistics.stream()
                .filter(stat -> "Compte épargne".equals(stat.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Le type 'Epargne' est manquant dans les statistiques"));
        assertThat(savingsStats.getNbrAccountByType()).isEqualTo(3L);
        assertThat(savingsStats.getAccountType()).isEqualTo("Compte épargne");

        NumberAccountStatisticProj checkingsStats = statistics.stream()
                .filter(stat -> "Compte courante".equals(stat.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Le type 'Courante' est manquant dans les statistiques"));
        assertThat(checkingsStats.getNbrAccountByType()).isEqualTo(3L);
        assertThat(checkingsStats.getAccountType()).isEqualTo("Compte courante");

        // Vérification que le type Business n'apparaît nulle part
        boolean hasBusiness = statistics.stream()
                .anyMatch(stat -> "Compte business".equals(stat.getAccountType()));
        assertThat(hasBusiness).isFalse();

    }

    @Test
    @DisplayName("Devrait retourner les statistiques des soldes de comptes groupés par type en excluant les comptes fermés")
    void shouldGetAccountStatisticSoldNoClosed() {
        List<SoldeAccountStatisticProj> statistics = accountRepository.getAccountStatisticSoldNoClosed(AccountStatusEnum.CLOSED);

        assertThat(statistics).isNotNull();
        assertThat(statistics).hasSize(3);

        SoldeAccountStatisticProj savingsStats = statistics.stream()
                .filter(stat -> "Compte épargne".equals(stat.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Le type 'Epargne' est manquant dans les statistiques"));
        assertThat(savingsStats.getSoldeAccountByType().compareTo(BigDecimal.valueOf(17700))).isEqualTo(0);
        assertThat(savingsStats.getAccountType()).isEqualTo("Compte épargne");

        SoldeAccountStatisticProj checkingsStats = statistics.stream()
                .filter(stat -> "Compte courante".equals(stat.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Le type 'Courante' est manquant dans les statistiques"));
        assertThat(checkingsStats.getSoldeAccountByType().compareTo(BigDecimal.valueOf(1100))).isEqualTo(0);
        assertThat(checkingsStats.getAccountType()).isEqualTo("Compte courante");

        SoldeAccountStatisticProj businessStats = statistics.stream()
                .filter(stat -> "Compte business".equals(stat.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Le type 'business' est manquant dans les statistiques"));
        assertThat(businessStats.getSoldeAccountByType().compareTo(BigDecimal.valueOf(400))).isEqualTo(0);
        assertThat(businessStats.getAccountType()).isEqualTo("Compte business");
    }

    @Test
    @DisplayName("Devrait retourner le total des soldes de comptes en excluant les comptes fermés")
    void shouldGetSoldTotalOfAccountNoClosed(){
        BigDecimal soldeTotal = accountRepository.getSoldTotalOfAccountNoClosed(AccountStatusEnum.CLOSED);
        assertThat(soldeTotal.compareTo(BigDecimal.valueOf(17700 + 1100 + 400))).isEqualTo(0);
    }

    @Test
    @DisplayName("Devrait retourner le nombres des comptes en excluant les comptes fermés")
    void shouldGetNbrTotalOfAccountNoClosed(){
        Long nbrAccount = accountRepository.getNbrTotalOfAccountNoClosed(AccountStatusEnum.CLOSED);
        assertThat(nbrAccount).isEqualTo(8);
    }

    //@Test
    //@DisplayName("Devrait retourner les pages des comptes dont le numéros de compte commence par un mots données")
    void shouldFindAllByAccountNumberStartsWith(){
        Pageable pageable = PageRequest.of(0,2);
        String start = "ACC-10";
        Page<AccountEntity> response = accountRepository.findAllByAccountNumberStartsWith(start, pageable);

        assertThat(response.getTotalPages()).isEqualTo(2);
        assertThat(response.getContent().size()).isEqualTo(2);
        assertThat(response.getNumber()).isEqualTo(0);
        assertThat(response.getContent())
                .extracting(AccountEntity::getAccountNumber).containsExactly("ACC-10-555456", "ACC-10-123456");
    }

    //@Test
    //@DisplayName("Devrait tester la methode getAllAccountByAccountNumberBegin() avec succès")
    void shouldGetAllAccountByAccountNumberBeginSuccessfully(){
        Pageable pageable = PageRequest.of(0,2);
        String start = "ACC-10";
        Page<AccountProjection>response = accountRepository.getAllAccountByAccountNumberBegin(start, pageable);

        assertThat(response.getTotalPages()).isEqualTo(2);
        assertThat(response.getTotalElements()).isEqualTo(3);
        assertThat(response.getContent().size()).isEqualTo(2);
        assertThat(response.getNumber()).isEqualTo(0);
        assertThat(response.getContent())
                .extracting(AccountProjection::getAccountNumber).containsExactly("ACC-10-443467", "ACC-10-123456");
        assertThat(response.getContent()).extracting(AccountProjection::getCurrencyCode).containsExactly("MGA","MGA");
        assertThat(response.getContent()).extracting(AccountProjection::getAccountTypeName).containsOnly("Compte courante");
    }

    @Test
    @DisplayName("Devrait retourner les comptes épargne et courante non closed")
    void shouldReturnListOfBusinessAndSavingAccount(){
        Pageable pageable = PageRequest.of(0,50);
        Page<AccountEntity> accounts = accountRepository.getAllByAccountTypeEntityCodeIn(Set.of("20","30"), AccountStatusEnum.CLOSED, pageable);

        assertThat(accounts.getContent().size()).isEqualTo(5);
        assertThat(
            accounts
                 .getContent()
                .stream()
                .filter(a -> a.getAccountTypeEntity().getCode().equals("20"))
                .toList()
                .size()
        ).isEqualTo(3);

        assertThat(accounts.getContent()).extracting(a -> a.getAccountTypeEntity().getCode()).contains("20","30");
        assertThat(accounts.getContent()).extracting(a -> a.getCurrencyEntity().getCode()).contains("MGA");
    }

    private AccountTypeEntity createAccountType(String code, String name) {
        return AccountTypeEntity.builder()
                .id(UUID.randomUUID())
                .code(code)
                .minimumBalance(BigDecimal.ZERO)
                .annualInterestRate(BigDecimal.ZERO)
                .accountFee(BigDecimal.ZERO)
                .name(name)
                .createdDate(LocalDate.now())
                .createdBy(UUID.randomUUID())
                .build();
    }

    private AccountEntity createAccount(AccountTypeEntity type, AccountStatusEnum status, String accountNumber, BigDecimal mgaBalance, LocalDate createdDate) {
        return AccountEntity.builder()
                .mgaBalance(mgaBalance)
                .overdraftLimit(BigDecimal.ZERO)
                .customerId(customerId)
                .balance(mgaBalance)
                .id(UUID.randomUUID())
                .accountStatus(status)
                .accountNumber(accountNumber)
                .accountTypeEntity(type)
                .currencyEntity(mgaCurrencyEntity)
                .createdDate(createdDate)
                .createdBy(UUID.randomUUID())
                .build();
    }
}
