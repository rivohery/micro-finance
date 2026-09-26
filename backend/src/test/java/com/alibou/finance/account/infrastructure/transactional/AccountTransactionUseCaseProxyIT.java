package com.alibou.finance.account.infrastructure.transactional;

import com.alibou.finance.BaseServiceIT;
import com.alibou.finance.account.application.port.command.TransferCommand;
import com.alibou.finance.account.application.port.result.TransferResult;
import com.alibou.finance.account.application.utils.TransactionOperationExecutor;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.exception.AccountNotFoundException;
import com.alibou.finance.account.domain.out.linker.CurrencyExchangePort;
import com.alibou.finance.account.domain.out.linker.CustomerLinkerPort;
import com.alibou.finance.account.domain.out.service.TransfertConfirmationExecutor;
import com.alibou.finance.account.domain.out.service.TransfertConfirmationInfo;
import com.alibou.finance.account.domain.vo.transaction.OriginalAmount;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.repository.AccountJpaRepository;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.repository.TransactionJpaRepository;
import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import com.alibou.finance.shared.domain.vo.Description;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.domain.vo.user.Email;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


public class AccountTransactionUseCaseProxyIT extends BaseServiceIT {

    @Autowired
    private AccountTransactionUseCaseProxy accountTransactionUseCase;
    @Autowired
    private TransactionJpaRepository transactionJpaRepository;
    @Autowired
    private AccountJpaRepository accountJpaRepository;
    @Autowired
    private TransactionOperationExecutor transactionOperationExecutor;

    @MockBean
    private CurrencyExchangePort currencyExchangePort;
    @MockBean
    private TransfertConfirmationExecutor transfertConfirmation;
    @MockBean
    private CustomerLinkerPort customerLinkerPort;

    AccountEntity sourceAccount;
    AccountEntity targetAccount;
    CurrencyCode mgaCurrency = new CurrencyCode("MGA");
    CurrencyCode usdCurrency = new CurrencyCode("USD");

    UserInfo userInfo = new UserInfo(
            UserId.generate(),
            new Username("alibou"),
            new Email("alibou@gmail.com")
    );


    @BeforeEach
    void setUp(){
        accountJpaRepository.deleteAll();
        transactionJpaRepository.deleteAll();


        sourceAccount = AccountEntity.builder()
                .id(UUID.randomUUID())
                .accountNumber("001-10-1234567890")
                .accountStatus(AccountStatusEnum.ACTIVE)
                .accountTypeId(UUID.randomUUID())
                .accountTypeCode("20")
                .accountTypeName("saving account")
                .annualInterestRate(BigDecimal.TEN)
                .balance(new BigDecimal("100.00"))
                .mgaBalance(BigDecimal.valueOf(500000.00))
                .createdBy(UUID.randomUUID())
                .currencyCode(usdCurrency.value())
                .currencyId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .overdraftLimit(BigDecimal.ZERO)
                .build();
        sourceAccount = accountJpaRepository.save(sourceAccount);

        targetAccount = AccountEntity.builder()
                .id(UUID.randomUUID())
                .accountNumber("001-20-1234567896")
                .accountStatus(AccountStatusEnum.ACTIVE)
                .accountTypeId(UUID.randomUUID())
                .accountTypeCode("10")
                .accountTypeName("checking account")
                .annualInterestRate(BigDecimal.TEN)
                .balance(BigDecimal.valueOf(10000.00))
                .mgaBalance(BigDecimal.valueOf(10000.00))
                .createdBy(UUID.randomUUID())
                .currencyCode(mgaCurrency.value())
                .currencyId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .overdraftLimit(BigDecimal.ZERO)
                .build();
        targetAccount = accountJpaRepository.save(targetAccount);
    }


    @Test
    @DisplayName("Test d'intégration pour la réussite de l'opération transfert")
    void transfer_shouldExecuteAllStepOfTransferWithSuccess(){
        assertThat(accountJpaRepository.findAll().size()).isEqualTo(2);

        TransferCommand transfertCommand = TransferCommand
                .builder()
                .sourceAccountNumber(new AccountNumber("001-10-1234567890"))
                .targetAccountNumber(new AccountNumber("001-20-1234567896"))
                .description(new Description("Test pour le transfert"))
                .userInfo(userInfo)
                .originalAmount(new OriginalAmount(BigDecimal.valueOf(10.0)))
                .build();

        when(customerLinkerPort.findCustomerIdByUserId(any(UserId.class))).thenReturn(new CustomerId(sourceAccount.getCustomerId()));
        when(currencyExchangePort.getExchangeRate("USD","MGA")).thenReturn(BigDecimal.valueOf(5000));
        when(currencyExchangePort.getExchangeRate("MGA", "MGA")).thenReturn(BigDecimal.ONE);

        TransferResult result = accountTransactionUseCase.transfert(transfertCommand);

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
        assertThat(withdrawTransaction.getOriginalAmount().value()).isEqualByComparingTo(BigDecimal.valueOf(10.00));
        assertThat(withdrawTransaction.getFinalAmount().value()).isEqualByComparingTo(BigDecimal.valueOf(10.00));
        assertThat(withdrawTransaction.getAccountNumber().value()).isEqualTo("001-10-1234567890");
        assertThat(withdrawTransaction.getReference().value()).isNotBlank();
        assertThat(withdrawTransaction.getDescription().value()).isEqualTo("Test pour le transfert");

        //--------deposit operation-------
        Account targetAccountResp = result.targetAccount();
        assertThat(targetAccountResp).isNotNull();
        assertThat(targetAccountResp.getAccountNumber().value()).isEqualTo("001-20-1234567896");
        assertThat(targetAccountResp.getBalance().value()).isEqualByComparingTo(BigDecimal.valueOf(10000.00 + (10.0 * 5000)));
        assertThat(targetAccountResp.getMgaBalance().value()).isEqualByComparingTo(BigDecimal.valueOf((10000.00 + (10.0 * 5000)) * 1));
        //-------
        Transaction depositTransaction = result.depositTransaction();
        assertThat(depositTransaction).isNotNull();
        assertThat(depositTransaction.getExchangeRate().value()).isEqualByComparingTo(BigDecimal.valueOf(5000));
        assertThat(depositTransaction.getOriginalAmount().value()).isEqualByComparingTo(BigDecimal.valueOf(10.00));
        assertThat(depositTransaction.getFinalAmount().value()).isEqualByComparingTo(BigDecimal.valueOf(10.00 * 5000));
        assertThat(depositTransaction.getAccountNumber().value()).isEqualTo("001-20-1234567896");
        assertThat(depositTransaction.getReference().value()).isNotBlank();
        assertThat(depositTransaction.getDescription().value()).isEqualTo("Test pour le transfert");

        assertThat(transactionJpaRepository.findAll().size()).isEqualTo(2);

        verify(transfertConfirmation).sendTransactionConfirmation(any(TransfertConfirmationInfo.class));

    }

    @Test
    @DisplayName("Pour tester le fonctionnement de l'annotation @Transactional")
    void transfer_shouldRollBackWithdrawTransactionOfSourceAccount(){
        TransferCommand transfertCommand = TransferCommand
                .builder()
                .sourceAccountNumber(new AccountNumber("001-10-1234567890"))
                .targetAccountNumber(new AccountNumber("001-20-1234567899"))// Numéros de compte invalid : compte inexistant
                .description(new Description("Test pour le transfert"))
                .userInfo(userInfo)
                .originalAmount(new OriginalAmount(BigDecimal.valueOf(10.0)))
                .build();
        when(customerLinkerPort.findCustomerIdByUserId(any(UserId.class))).thenReturn(new CustomerId(sourceAccount.getCustomerId()));
        when(currencyExchangePort.getExchangeRate("USD","MGA")).thenReturn(BigDecimal.valueOf(5000));

        assertThatThrownBy(() -> accountTransactionUseCase.transfert(transfertCommand))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Compte introuvable: numéros de compte invalid: 001-20-1234567899");

        assertThat(transactionJpaRepository.findAll().size()).isEqualTo(0);
        AccountEntity sourceAccountResult = accountJpaRepository.findByAccountNumber("001-10-1234567890").orElseThrow();
        assertThat(sourceAccountResult.getBalance()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(sourceAccountResult.getMgaBalance()).isEqualByComparingTo(new BigDecimal("500000.00"));

    }


}
