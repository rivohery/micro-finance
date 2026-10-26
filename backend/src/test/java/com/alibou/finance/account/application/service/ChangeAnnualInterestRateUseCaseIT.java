package com.alibou.finance.account.application.service;


import com.alibou.finance.BaseServiceIT;
import com.alibou.finance.account.application.port.usecase.ChangeAnnualInterestRateUseCase;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.repository.AccountJpaRepository;
import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeAnnualInterestRateUseCaseIT extends BaseServiceIT {
    @Autowired
    private AccountJpaRepository accountJpaRepository;

    @Autowired
    private ChangeAnnualInterestRateUseCase changeAnnualInterestRateUseCase;

    AccountTypeId savingAccountTypeId = AccountTypeId.generate();
    AccountTypeId businessAccountTypeId = AccountTypeId.generate();
    AccountTypeId checkingAccountTypeId = AccountTypeId.generate();
    UUID customerId;

    @BeforeEach
    void setUp(){
        customerId = UUID.randomUUID();
        accountJpaRepository.deleteAll();

        AccountEntity ae1 = createAccount(savingAccountTypeId.value(), "compte épargne", "20", AccountStatusEnum.ACTIVE, "CE1-010");
        AccountEntity ae2 = createAccount(savingAccountTypeId.value(),"compte épargne", "20", AccountStatusEnum.ACTIVE, "CE1-011");
        AccountEntity ae3 = createAccount(checkingAccountTypeId.value(), "compte courante", "10", AccountStatusEnum.ACTIVE, "CE1-012");
        AccountEntity ae4 = createAccount(checkingAccountTypeId.value(),"compte courante", "10", AccountStatusEnum.ACTIVE, "CE1-013");
        AccountEntity ae5 = createAccount(businessAccountTypeId.value(),"compte business", "30", AccountStatusEnum.ACTIVE, "CE1-014");
        AccountEntity ae6 = createAccount(savingAccountTypeId.value(),"compte épargne", "20", AccountStatusEnum.ACTIVE, "CE1-015");
        AccountEntity ae7 = createAccount(savingAccountTypeId.value(),"compte épargne", "20", AccountStatusEnum.CLOSED, "CE1-016");
        accountJpaRepository.saveAll(Set.of(ae1,ae2,ae3,ae4,ae5,ae6,ae7));

    }

    @Test
    void should_changeAnnulaInterestRateOfSavingAccount(){
        changeAnnualInterestRateUseCase.execute(savingAccountTypeId, new InterestRate(new BigDecimal("7.30")));

        List<AccountEntity>accounts = accountJpaRepository.findAll();
        List<AccountEntity>savingAccounts = accounts.stream()
                .filter(ae -> ae.getAccountTypeCode().equals("20") && ae.getAccountStatus() != AccountStatusEnum.CLOSED)
                .toList();

        assertThat(savingAccounts).hasSize(3);
        assertThat(savingAccounts).extracting(ae -> ae.getAnnualInterestRate()).containsOnly(new BigDecimal("7.3000"));
        assertThat(savingAccounts).extracting(ae -> ae.getVersion()).containsOnly(2L);
        assertThat(savingAccounts).extracting(AccountEntity::getAccountTypeId).containsOnly(savingAccountTypeId.value());

        List<AccountEntity>checkingAccounts = accounts.stream()
                .filter(ae -> ae.getAccountTypeCode().equals("10"))
                .toList();
        assertThat(checkingAccounts).extracting(ae -> ae.getAnnualInterestRate()).containsOnly(new BigDecimal("3.6500"));
        assertThat(checkingAccounts).extracting(ae -> ae.getVersion()).containsOnly(1L);

        List<AccountEntity>businessAccounts = accounts.stream()
                .filter(ae -> ae.getAccountTypeCode().equals("30"))
                .toList();
        assertThat(businessAccounts).extracting(ae -> ae.getAnnualInterestRate()).containsOnly(new BigDecimal("3.6500"));
        assertThat(businessAccounts).extracting(ae -> ae.getVersion()).containsOnly(1L);
    }

    private AccountEntity createAccount(UUID accountTYpeId, String accountTypeName, String accountTypeCode,AccountStatusEnum status, String accountNumber) {
        return AccountEntity.builder()
                .mgaBalance(BigDecimal.ZERO)
                .overdraftLimit(BigDecimal.ZERO)
                .customerId(customerId)
                .annualInterestRate(new BigDecimal("3.65"))
                .balance(BigDecimal.ZERO)
                .id(UUID.randomUUID())
                .accountStatus(status)
                .accountNumber(accountNumber)
                .accountTypeName(accountTypeName)
                .accountTypeCode(accountTypeCode)
                .accountTypeId(accountTYpeId)
                .currencyCode("MGA")
                .currencyId(UUID.randomUUID())
                .createdDate(LocalDate.now())
                .createdBy(UUID.randomUUID())
                .version(1L)
                .build();
    }
}
