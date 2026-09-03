package com.alibou.finance.account.infrastructure.proxy;

import com.alibou.finance.account.application.port.dto.command.TransferCommand;
import com.alibou.finance.account.application.port.dto.output.TransferResult;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.account.domain.exception.AccountNotFoundException;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.out.service.TransfertConfirmationPort;
import com.alibou.finance.account.domain.out.service.dto.TransfertConfirmationInfo;
import com.alibou.finance.account.domain.vo.*;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.repository.AccountJpaRepository;
import com.alibou.finance.account.infrastructure.transactional.AccountTransactionUseCaseProxy;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.entity.AccountTypeEntity;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.repository.AccountTypeJpaRepository;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.vo.Username;
import com.alibou.finance.currency.infrastructure.adapter.out.entity.CurrencyEntity;
import com.alibou.finance.currency.infrastructure.adapter.out.repository.CurrencyJpaRepository;
import com.alibou.finance.customer.application.port.CustomerConsultationUseCase;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.log.domain.vo.transaction.OriginalAmount;
import com.alibou.finance.log.infrastructure.adapter.out.persistence.repository.TransactionJpaRepository;
import com.alibou.finance.shared.vo.domain.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AccountTransactionUseCaseProxyIntegrationTest {

    @Autowired
    private AccountTransactionUseCaseProxy accountTransaction;
    @Autowired
    private TransactionJpaRepository transactionJpaRepository;
    @Autowired
    private AccountJpaRepository accountJpaRepository;
    @Autowired
    private AccountTypeJpaRepository accountTypeJpaRepository;
    @Autowired
    private CurrencyJpaRepository currencyJpaRepository;

    @MockBean
    private CustomerConsultationUseCase customerService;
    @MockBean
    private CurrencyExchangePort currencyExchangePort;
    @MockBean
    private TransfertConfirmationPort transfertConfirmationService;

    AccountEntity sourceAccount;
    AccountEntity targetAccount;

    User userConnected;


    @BeforeEach
    void setUp(){
        accountJpaRepository.deleteAll();
        accountTypeJpaRepository.deleteAll();
        currencyJpaRepository.deleteAll();
        transactionJpaRepository.deleteAll();


        userConnected = User.builder().username(new Username("alibou")).build();

        AccountTypeEntity savingAccount = AccountTypeEntity.builder()
                .accountFee(BigDecimal.ZERO)
                .annualInterestRate(BigDecimal.valueOf(0.2))
                .code("20")
                .minimumBalance(BigDecimal.ZERO)
                .id(UUID.randomUUID())
                .name("compte épargne")
                .createdBy(UUID.randomUUID())
                .createdDate(LocalDate.now())
                .build();
        savingAccount = accountTypeJpaRepository.save(savingAccount);

        CurrencyEntity mga = CurrencyEntity
                .builder()
                .name("Ariary")
                .id(UUID.randomUUID())
                .enable(true)
                .code("MGA")
                .build();
        CurrencyEntity usd = CurrencyEntity
                .builder()
                .name("Dollar")
                .id(UUID.randomUUID())
                .enable(true)
                .code("USD")
                .build();
        mga = currencyJpaRepository.save(mga);
        usd = currencyJpaRepository.save(usd);

        sourceAccount = AccountEntity.builder()
                .id(UUID.randomUUID())
                .accountNumber("001-10-1234567890")
                .accountStatus(AccountStatusEnum.ACTIVE)
                .accountTypeEntity(savingAccount)
                .balance(new BigDecimal("100.00"))
                .mgaBalance(BigDecimal.valueOf(500000.00))
                .createdBy(UUID.randomUUID())
                .currencyEntity(usd)
                .customerId(UUID.randomUUID())
                .overdraftLimit(BigDecimal.ZERO)
                .build();
        sourceAccount = accountJpaRepository.save(sourceAccount);

        targetAccount = AccountEntity.builder()
                .id(UUID.randomUUID())
                .accountNumber("001-20-1234567896")
                .accountStatus(AccountStatusEnum.ACTIVE)
                .accountTypeEntity(savingAccount)
                .balance(BigDecimal.valueOf(10000.00))
                .mgaBalance(BigDecimal.valueOf(10000.00))
                .createdBy(UUID.randomUUID())
                .currencyEntity(mga)
                .customerId(UUID.randomUUID())
                .overdraftLimit(BigDecimal.ZERO)
                .build();
        targetAccount = accountJpaRepository.save(targetAccount);



    }

    @Test
    @DisplayName("Test d'intégration pour la réussite de l'opération transfert")
    void shouldTransferWithSuccess(){
        assertThat(accountJpaRepository.findAll().size()).isEqualTo(2);

        TransferCommand transfertCommand = TransferCommand
                .builder()
                .sourceAccountNumber(new AccountNumber("001-10-1234567890"))
                .targetAccountNumber(new AccountNumber("001-20-1234567896"))
                .description(new Description("Test pour le transfert"))
                .user(userConnected)
                .originalAmount(new OriginalAmount(BigDecimal.valueOf(10.0)))
                .build();

        when(customerService.findCustomerIdByUser(any(User.class))).thenReturn(new CustomerId(sourceAccount.getCustomerId()));
        when(currencyExchangePort.getExchangeRate("USD","MGA")).thenReturn(BigDecimal.valueOf(5000));
        when(currencyExchangePort.getExchangeRate("MGA", "MGA")).thenReturn(BigDecimal.ONE);
        when(transfertConfirmationService.sendTransactionConfirmation(any(TransfertConfirmationInfo.class))).thenReturn(1);

        TransferResult result = accountTransaction.transfert(transfertCommand);

        //--------withdraw operation-----
        Account sourceAccountResp = result.sourceAccount();
        assertThat(sourceAccountResp).isNotNull();
        assertThat(sourceAccountResp.getAccountNumber().value()).isEqualTo("001-10-1234567890");
        assertThat(sourceAccountResp.getBalance().value().compareTo(BigDecimal.valueOf(100.00 - 10.0))).isEqualTo(0);
        assertThat(sourceAccountResp.getMgaBalance().value().compareTo(BigDecimal.valueOf(90.00 * 5000.00))).isEqualTo(0);
        //-------
        Transaction withdrawTransaction = result.withdrawTransaction();
        assertThat(withdrawTransaction).isNotNull();
        assertThat(withdrawTransaction.getExchangeRate().value().compareTo(BigDecimal.ONE)).isEqualTo(0);
        assertThat(withdrawTransaction.getOriginalAmount().value().compareTo(BigDecimal.valueOf(10.00))).isEqualTo(0);
        assertThat(withdrawTransaction.getFinalAmount().value().compareTo(BigDecimal.valueOf(10.00))).isEqualTo(0);
        assertThat(withdrawTransaction.getAccountNumber().value()).isEqualTo("001-10-1234567890");
        assertThat(withdrawTransaction.getReference().value()).isNotBlank();
        assertThat(withdrawTransaction.getDescription().value()).isEqualTo("Test pour le transfert");

        //--------deposit operation-------
        Account targetAccountResp = result.targetAccount();
        assertThat(targetAccountResp).isNotNull();
        assertThat(targetAccountResp.getAccountNumber().value()).isEqualTo("001-20-1234567896");
        assertThat(targetAccountResp.getBalance().value().compareTo(BigDecimal.valueOf(10000.00 + (10.0 * 5000)))).isEqualTo(0);
        assertThat(targetAccountResp.getMgaBalance().value().compareTo(BigDecimal.valueOf((10000.00 + (10.0 * 5000)) * 1))).isEqualTo(0);
        //-------
        Transaction depositTransaction = result.depositTransaction();
        assertThat(depositTransaction).isNotNull();
        assertThat(depositTransaction.getExchangeRate().value().compareTo(BigDecimal.valueOf(5000))).isEqualTo(0);
        assertThat(depositTransaction.getOriginalAmount().value().compareTo(BigDecimal.valueOf(10.00))).isEqualTo(0);
        assertThat(depositTransaction.getFinalAmount().value().compareTo(BigDecimal.valueOf(10.00 * 5000))).isEqualTo(0);
        assertThat(depositTransaction.getAccountNumber().value()).isEqualTo("001-20-1234567896");
        assertThat(depositTransaction.getReference().value()).isNotBlank();
        assertThat(depositTransaction.getDescription().value()).isEqualTo("Test pour le transfert");

        assertThat(transactionJpaRepository.findAll().size()).isEqualTo(2);

    }

    @Test
    @DisplayName("Pour tester le fonctionnement de l'annotation @Transactional")
    void transfert_testTransactionSuccess(){
        TransferCommand transfertCommand = TransferCommand
                .builder()
                .sourceAccountNumber(new AccountNumber("001-10-1234567890"))
                .targetAccountNumber(new AccountNumber("001-20-1234567899"))// Numéros de compte invalid : compte inexistant
                .description(new Description("Test pour le transfert"))
                .user(userConnected)
                .originalAmount(new OriginalAmount(BigDecimal.valueOf(10.0)))
                .build();
        when(customerService.findCustomerIdByUser(any(User.class))).thenReturn(new CustomerId(sourceAccount.getCustomerId()));
        when(currencyExchangePort.getExchangeRate("USD","MGA")).thenReturn(BigDecimal.valueOf(5000));

        assertThatThrownBy(() -> accountTransaction.transfert(transfertCommand))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Compte introuvable: numéros de compte invalid: 001-20-1234567899");

        assertThat(transactionJpaRepository.findAll().size()).isEqualTo(0);
        AccountEntity sourceAccountResult = accountJpaRepository.findByAccountNumber("001-10-1234567890").orElse(null);
        assertThat(sourceAccountResult.getBalance().compareTo(new BigDecimal("100.00"))).isEqualTo(0);
        assertThat(sourceAccountResult.getMgaBalance().compareTo(new BigDecimal("500000.00"))).isEqualTo(0);

    }
}
