package com.alibou.finance.account.infrastructure.out.repository;

import com.alibou.finance.BaseRepositoryIT;
import com.alibou.finance.JpaAuditingTestConfig;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaAuditingTestConfig.class)
public class AccountJpqlQueryIT extends BaseRepositoryIT {
/*
    private static final String JPQL_IN_MGA_BALANCE_JOB_BATCH =
            "SELECT a FROM AccountEntity a " +
                    "WHERE a.accountStatus NOT IN :status " +
                    "ORDER BY a.id ASC";

    private static final String JPQL_IN_INTEREST_RATE_JOB_BATCH =
            "SELECT a FROM AccountEntity a " +
                    "WHERE a.accountTypeCode IN :codes " +
                    "AND a.accountStatus =:status " +
                    "ORDER BY a.id ASC";


    @Autowired
    private EntityManager entityManager;


    @BeforeEach
    void setUp(){
        AccountTypeEntity checkingAccount = createMinType("10", "Compte courante");
        AccountTypeEntity savingAccount = createMinType("20", "Compte épargne");
        AccountTypeEntity businessAccount = createMinType("30", "Compte business");
        Set.of(checkingAccount, savingAccount, businessAccount).forEach(at -> entityManager.persist(at));

        CurrencyCode eurCurrency = new CurrencyCode("EUR");
        CurrencyCode mgaCurrency = new CurrencyCode("MGA");

        AccountEntity acc1 = createMinAccount(savingAccount, mgaCurrency, "ACC-1", AccountStatusEnum.CLOSED);
        AccountEntity acc2 = createMinAccount(checkingAccount, eurCurrency, "ACC-2", AccountStatusEnum.ACTIVE);
        AccountEntity acc3 = createMinAccount(savingAccount, eurCurrency, "ACC-3", AccountStatusEnum.SUSPENDED);
        AccountEntity acc4 = createMinAccount(businessAccount, mgaCurrency, "ACC-4", AccountStatusEnum.PENDING);
        AccountEntity acc5 = createMinAccount(checkingAccount, mgaCurrency, "ACC-5", AccountStatusEnum.ACTIVE);
        AccountEntity acc6 = createMinAccount(businessAccount, mgaCurrency, "ACC-6", AccountStatusEnum.ACTIVE);

        Set.of(acc1, acc2, acc3, acc4, acc5, acc6).forEach( acc -> entityManager.persist(acc));

        entityManager.flush();
        entityManager.clear(); // important : vide le cache de 1er niveau pour forcer un vrai SELECT

    }

    @Test
    void jpqlMgaBalanceJob_shouldSuccess(){
        Set<AccountStatusEnum> params = Set.of(AccountStatusEnum.PENDING, AccountStatusEnum.CLOSED);

        TypedQuery<AccountEntity> query = entityManager.createQuery(JPQL_IN_MGA_BALANCE_JOB_BATCH, AccountEntity.class);
        query.setParameter("status", params);
        List<AccountEntity> accounts = query.getResultList();

        assertThat(accounts).hasSize(4);
        assertThat(accounts).extracting(a -> a.getAccountNumber()).containsExactlyInAnyOrder("ACC-2", "ACC-5", "ACC-3", "ACC-6");

        assertThat(accounts).extracting(a -> a.getCreatedDate()).isNotNull();
        assertThat(accounts).extracting(a -> a.getCreatedDate()).contains(
                LocalDateTime.of(2024, 1, 1, 12, 0, 0).toLocalDate()
        );
        assertThat(accounts).extracting(a -> a.getCreatedBy().toString()).containsOnly("00000000-0000-0000-0000-000000000000");
    }

    @Test
    void jpqlInterestRateJob_shouldSuccess(){
        Set<String> codes = Set.of("20", "30");//type
        AccountStatusEnum status = AccountStatusEnum.ACTIVE;

        TypedQuery<AccountEntity>query = entityManager.createQuery(JPQL_IN_INTEREST_RATE_JOB_BATCH, AccountEntity.class);
        query.setParameter("codes", codes);
        query.setParameter("status", status);
        List<AccountEntity>accounts = query.getResultList();

        assertThat(accounts).hasSize(1);
        assertThat(accounts).extracting(a -> a.getAccountNumber()).containsOnly("ACC-6");

    }

    private AccountEntity createMinAccount(AccountTypeEntity accountType, CurrencyCode currencyCode, String accountNumber, AccountStatusEnum status){
        return AccountEntity.builder()
                .accountNumber(accountNumber)
                .id(UUID.randomUUID())
                .accountStatus(status)
                .balance(new BigDecimal("2000.0"))
                .mgaBalance(new BigDecimal("2000.0"))
                .accountTypeId(accountType.getId())
                .currencyCode(currencyCode.value())
                .currencyId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .overdraftLimit(BigDecimal.ZERO)
                .version(1L)
                .build();
    }

    private AccountTypeEntity createMinType(String code, String name){
        return AccountTypeEntity.builder()
                .accountFee(BigDecimal.ZERO)
                .id(UUID.randomUUID())
                .annualInterestRate(new BigDecimal("0.0001"))
                .minimumBalance(BigDecimal.ZERO)
                .code(code)
                .name(name)
                .build();
    }


 */
}
