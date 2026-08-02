package com.alibou.finance.account.application;

import com.alibou.finance.account.application.port.dto.input.TransactionInput;
import com.alibou.finance.account.application.service.AccountTransactionServiceApplication;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.OverdraftLimit;
import com.alibou.finance.account.domain.exception.ThirdPartyServiceException;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.out.service.ReferenceGenerator;
import com.alibou.finance.account.domain.out.service.TransfertConfirmationPort;
import com.alibou.finance.account.domain.out.service.dto.TransfertConfirmationInfo;
import com.alibou.finance.account.domain.vo.*;
import com.alibou.finance.auth.domain.model.User;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.auth.domain.vo.Username;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.currency.domain.vo.CurrencyName;
import com.alibou.finance.customer.application.port.CustomerUseCase;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.shared.error.domain.IllegalOperationException;
import com.alibou.finance.shared.vo.domain.Description;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.vo.transaction.OriginalAmount;
import com.alibou.finance.account.domain.vo.transaction.TransactionCurrencyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountTransactionServiceApplicationTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CurrencyExchangePort currencyExchangePort;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ReferenceGenerator referenceGenerator;
    @Mock
    private CustomerUseCase customerService;
    @Mock
    private TransfertConfirmationPort transfertConfirmationService;
    @InjectMocks
    private AccountTransactionServiceApplication accountTransactionService;

    Currency usdCurrency;
    Currency mgaCurrency;
    Account account;
    TransactionInput depositInput;
    TransactionInput withdrawInput;
    User userConnected;

    final String now = LocalDate.now().toString().replace("-", "");

    @BeforeEach
    void setUp(){
        usdCurrency = Currency.builder().code(new CurrencyCode("USD")).name(new CurrencyName("Dollar USA")).build();
        mgaCurrency = Currency.builder().code(new CurrencyCode("MGA")).name(new CurrencyName("Ariary")).build();
        account = Account.builder()
                .accountNumber(new AccountNumber("001-10-1234567890"))
                .currency(usdCurrency)
                .accountStatus(AccountStatus.active())
                .mgaBalance(new MgaBalance(BigDecimal.valueOf(50000.00)))
                .balance(new Balance(BigDecimal.valueOf(10.00)))
                .overdraftLimit(new OverdraftLimit(BigDecimal.valueOf(2.0)))
                .customerId(new CustomerId(UUID.randomUUID()))
                .build();
        userConnected = User.builder().userId(UserId.generate()).username(new Username("alibou")).build();
    }

    @Test
    @DisplayName("Devrait effectuer l'opération du dépôts avec success")
    void shouldDepositWithSuccess(){
        depositInput = TransactionInput.builder()
                .concernedAccountNumber(new AccountNumber("001-10-1234567890"))
                .description(new Description("Simple test pour tester le dépôts,monnaie transaction même que celui du compte"))
                .user(User.builder().username(new Username("john")).build())
                .originalAmount(new OriginalAmount(BigDecimal.valueOf(30.45)))
                .transactionCurrencyCode(new TransactionCurrencyCode("USD"))
                .build();

        when(accountRepository.findByAccountNumber(depositInput.getConcernedAccountNumber())).thenReturn(Optional.of(account));
        when(currencyExchangePort.getExchangeRate(depositInput.getTransactionCurrencyCode().value(), "USD")).thenReturn(BigDecimal.ONE);
        when(currencyExchangePort.getExchangeRate(account.getCurrency().getCode().value(), "MGA")).thenReturn(BigDecimal.valueOf(5000.00));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(referenceGenerator.generateReferenceCharacter(6)).thenReturn("A12345");
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Map<String,Object>response = accountTransactionService.deposit(depositInput);

        Account accountSaved = (Account) response.get("account");
        Transaction transactionSaved = (Transaction) response.get("transaction");
        assertThat(accountSaved).isNotNull();
        assertThat(transactionSaved).isNotNull();
        assertThat(accountSaved.getBalance().value().compareTo(BigDecimal.valueOf(40.45))).isEqualTo(0);
        assertThat(accountSaved.getMgaBalance().value().compareTo(BigDecimal.valueOf(40.45 * 5000.00))).isEqualTo(0);
        assertThat(transactionSaved.getExchangeRate().value().compareTo(BigDecimal.ONE)).isEqualTo(0);
        assertThat(transactionSaved.getOriginalAmount().value().compareTo(BigDecimal.valueOf(30.45))).isEqualTo(0);
        assertThat(transactionSaved.getFinalAmount().value().compareTo(BigDecimal.valueOf(30.45))).isEqualTo(0);
        assertThat(transactionSaved.getAccountNumber().value()).isEqualTo("001-10-1234567890");

        assertThat(transactionSaved.getReference().value()).isEqualTo("DEP-"+ now +"-A12345");
        assertThat(transactionSaved.getDescription().value()).isEqualTo("Simple test pour tester le dépôts,monnaie transaction même que celui du compte");

        verify(accountRepository, times(1)).findByAccountNumber(any(AccountNumber.class));
        verify(currencyExchangePort, times(2)).getExchangeRate(anyString(), anyString());
        verify(accountRepository).save(any(Account.class));
        verify(referenceGenerator).generateReferenceCharacter(6);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Devrait lever une exception dans le cas ôu on a un problème de connexion par exemple")
    void deposit_shouldThrowThirdPartyServiceException(){
        depositInput = TransactionInput.builder()
                .concernedAccountNumber(new AccountNumber("001-10-1234567890"))
                .description(new Description("Simple test pour tester le dépôts,monnaie transaction même que celui du compte"))
                .user(User.builder().username(new Username("john")).build())
                .originalAmount(new OriginalAmount(BigDecimal.valueOf(30.45)))
                .transactionCurrencyCode(new TransactionCurrencyCode("USD"))
                .build();

        when(accountRepository.findByAccountNumber(depositInput.getConcernedAccountNumber())).thenReturn(Optional.of(account));
        when(currencyExchangePort.getExchangeRate(depositInput.getTransactionCurrencyCode().value(), "USD"))
                .thenThrow(new ThirdPartyServiceException("Le service de conversion monétaire est temporairement indisponible. Veuillez réessayer plus tard."));

        assertThatThrownBy(
                ()-> accountTransactionService.deposit(depositInput)
        ).isInstanceOf(ThirdPartyServiceException.class)
         .hasMessage("Le service de conversion monétaire est temporairement indisponible. Veuillez réessayer plus tard.");


        verify(accountRepository, times(1)).findByAccountNumber(any(AccountNumber.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(referenceGenerator, never()).generateReferenceCharacter(6);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }


    @Test
    @DisplayName("Devrait effectuer l'opération du retraits avec success")
    void shouldWithdrawWithSuccess(){
        withdrawInput = TransactionInput.builder()
                .concernedAccountNumber(new AccountNumber("001-10-1234567890"))
                .description(new Description("Simple test pour le retraits; monnaie du transaction different de celle du compte"))
                .user(User.builder().username(new Username("john")).build())
                .originalAmount(new OriginalAmount(BigDecimal.valueOf(5000)))
                .transactionCurrencyCode(new TransactionCurrencyCode("MGA"))
                .build();


        when(accountRepository.findByAccountNumber(withdrawInput.getConcernedAccountNumber())).thenReturn(Optional.of(account));
        when(currencyExchangePort.getExchangeRate(withdrawInput.getTransactionCurrencyCode().value(), "USD")).thenReturn(BigDecimal.valueOf(0.0005));
        when(currencyExchangePort.getExchangeRate(account.getCurrency().getCode().value(), "MGA")).thenReturn(BigDecimal.valueOf(5000.00));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(referenceGenerator.generateReferenceCharacter(6)).thenReturn("A12345");
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Map<String,Object>response = accountTransactionService.withdraw(withdrawInput);

        Account accountSaved = (Account) response.get("account");
        Transaction transactionSaved = (Transaction) response.get("transaction");
        assertThat(accountSaved).isNotNull();
        assertThat(transactionSaved).isNotNull();
        assertThat(accountSaved.getBalance().value().compareTo(BigDecimal.valueOf(10.00 - (5000 * 0.0005)))).isEqualTo(0);
        assertThat(accountSaved.getMgaBalance().value().compareTo(BigDecimal.valueOf((10.00 - (5000 * 0.0005)) * 5000.00 ))).isEqualTo(0);
        assertThat(transactionSaved.getExchangeRate().value().compareTo(BigDecimal.valueOf(0.0005))).isEqualTo(0);
        assertThat(transactionSaved.getOriginalAmount().value().compareTo(BigDecimal.valueOf(5000))).isEqualTo(0);
        assertThat(transactionSaved.getFinalAmount().value().compareTo(BigDecimal.valueOf(5000 * 0.0005))).isEqualTo(0);
        assertThat(transactionSaved.getAccountNumber().value()).isEqualTo("001-10-1234567890");

        assertThat(transactionSaved.getReference().value()).isEqualTo("WIT-" + now + "-A12345");
        assertThat(transactionSaved.getDescription().value()).isEqualTo("Simple test pour le retraits; monnaie du transaction different de celle du compte");

        verify(accountRepository, times(1)).findByAccountNumber(any(AccountNumber.class));
        verify(currencyExchangePort, times(2)).getExchangeRate(anyString(), anyString());
        verify(accountRepository).save(any(Account.class));
        verify(referenceGenerator).generateReferenceCharacter(6);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Devrait lever une exception dans le cas ôu le solde est insuffisant")
    void withdraw_shouldThrowIllegalOperationException(){
        withdrawInput = TransactionInput.builder()
                .concernedAccountNumber(new AccountNumber("001-10-1234567890"))
                .description(new Description("Simple test pour le retraits; monnaie du transaction different de celle du compte"))
                .user(User.builder().username(new Username("john")).build())
                .originalAmount(new OriginalAmount(BigDecimal.valueOf(500000)))
                .transactionCurrencyCode(new TransactionCurrencyCode("MGA"))
                .build();


        when(accountRepository.findByAccountNumber(withdrawInput.getConcernedAccountNumber())).thenReturn(Optional.of(account));
        when(currencyExchangePort.getExchangeRate(withdrawInput.getTransactionCurrencyCode().value(), "USD")).thenReturn(BigDecimal.valueOf(0.0005));

        assertThatThrownBy(
                () -> accountTransactionService.withdraw(withdrawInput)
        ).isInstanceOf(IllegalOperationException.class)
         .hasMessage("Solde du compte insuffisant : " + account.getBalance().value().doubleValue());


        verify(accountRepository, times(1)).findByAccountNumber(any(AccountNumber.class));
        verify(currencyExchangePort, times(1)).getExchangeRate(anyString(), anyString());
        verify(accountRepository, never()).save(any(Account.class));
        verify(referenceGenerator, never()).generateReferenceCharacter(6);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Devrait effectuer le transfert avec success")
    void shouldTransfertWithSuccess(){
        Account sourceAccount = Account.builder()//=>USD
                .accountNumber(new AccountNumber("001-10-1234567890"))
                .currency(usdCurrency)
                .accountStatus(AccountStatus.active())
                .mgaBalance(new MgaBalance(BigDecimal.valueOf(500000.00)))
                .balance(new Balance(BigDecimal.valueOf(100.00)))
                .overdraftLimit(new OverdraftLimit(BigDecimal.valueOf(2.0)))
                .customerId(new CustomerId(UUID.randomUUID()))
                .build();
        Account targetAccount = Account.builder()//=>MGA
                .accountNumber(new AccountNumber("001-20-1234567896"))
                .currency(mgaCurrency)
                .accountStatus(AccountStatus.active())
                .mgaBalance(new MgaBalance(BigDecimal.valueOf(10000.00)))
                .balance(new Balance(BigDecimal.valueOf(10000.00)))
                .overdraftLimit(new OverdraftLimit(BigDecimal.valueOf(1.0)))
                .build();


        TransactionInput transfert = TransactionInput
                .builder()
                .concernedAccountNumber(new AccountNumber("001-10-1234567890"))
                .targetAccountNumber(new AccountNumber("001-20-1234567896"))
                .description(new Description("Test pour le transfert"))
                .user(userConnected)
                .originalAmount(new OriginalAmount(BigDecimal.valueOf(10.0)))
                .build();

        //withdraw operation
        when(accountRepository.findByAccountNumber(new AccountNumber("001-10-1234567890"))).thenReturn(Optional.of(sourceAccount));
        when(customerService.findCustomerIdByUser(any(User.class))).thenReturn(sourceAccount.getCustomerId());
        //common operation
        when(currencyExchangePort.getExchangeRate("USD","MGA")).thenReturn(BigDecimal.valueOf(5000));
        ArgumentCaptor<Account> sourceAccountCaptor = ArgumentCaptor.forClass(Account.class);
        when(accountRepository.save(sourceAccountCaptor.capture())).thenAnswer(i -> i.getArgument(0));
        when(referenceGenerator.generateReferenceCharacter(6)).thenReturn("A12345");
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
        //deposit operation
        when(accountRepository.findByAccountNumber(new AccountNumber("001-20-1234567896"))).thenReturn(Optional.of(targetAccount));
        when(currencyExchangePort.getExchangeRate("MGA", "MGA")).thenReturn(BigDecimal.ONE);

        //final
        when(transfertConfirmationService.sendTransactionConfirmation(any(TransfertConfirmationInfo.class))).thenReturn(1);

        Map<String, Object> result = accountTransactionService.transfert(transfert);

        //--------withdraw operation-----
        Account sourceAccountResp = (Account) result.get("sourceAccount");
        assertThat(sourceAccountResp).isNotNull();
        assertThat(sourceAccountResp.getBalance().value().compareTo(BigDecimal.valueOf(100.00 - 10.0))).isEqualTo(0);
        assertThat(sourceAccountResp.getMgaBalance().value().compareTo(BigDecimal.valueOf(90.00 * 5000.00))).isEqualTo(0);
        //-------
        Transaction withdrawTransaction = (Transaction)result.get("withdrawTransaction");
        assertThat(withdrawTransaction).isNotNull();
        assertThat(withdrawTransaction.getExchangeRate().value().compareTo(BigDecimal.ONE)).isEqualTo(0);
        assertThat(withdrawTransaction.getOriginalAmount().value().compareTo(BigDecimal.valueOf(10.00))).isEqualTo(0);
        assertThat(withdrawTransaction.getFinalAmount().value().compareTo(BigDecimal.valueOf(10.00))).isEqualTo(0);
        assertThat(withdrawTransaction.getAccountNumber().value()).isEqualTo("001-10-1234567890");
        assertThat(withdrawTransaction.getReference().value()).isEqualTo("WIT-"+ now +"-A12345");
        assertThat(withdrawTransaction.getDescription().value()).isEqualTo("Test pour le transfert");

        //--------deposit operation-------
        Account targetAccountResp = (Account) result.get("targetAccount");
        assertThat(targetAccountResp).isNotNull();
        assertThat(targetAccountResp.getBalance().value().compareTo(BigDecimal.valueOf(10000.00 + (10.0 * 5000)))).isEqualTo(0);
        assertThat(targetAccountResp.getMgaBalance().value().compareTo(BigDecimal.valueOf((10000.00 + (10.0 * 5000)) * 1))).isEqualTo(0);
        //-------
        Transaction depositTransaction = (Transaction)result.get("depositTransaction");
        assertThat(depositTransaction).isNotNull();
        assertThat(depositTransaction.getExchangeRate().value().compareTo(BigDecimal.valueOf(5000))).isEqualTo(0);
        assertThat(depositTransaction.getOriginalAmount().value().compareTo(BigDecimal.valueOf(10.00))).isEqualTo(0);
        assertThat(depositTransaction.getFinalAmount().value().compareTo(BigDecimal.valueOf(10.00 * 5000))).isEqualTo(0);
        assertThat(depositTransaction.getAccountNumber().value()).isEqualTo("001-20-1234567896");
        assertThat(depositTransaction.getReference().value()).isEqualTo("DEP-"+ now +"-A12345");
        assertThat(depositTransaction.getDescription().value()).isEqualTo("Test pour le transfert");

        verify(accountRepository, times(2)).save(any(Account.class));
        verify(currencyExchangePort, times(3)).getExchangeRate(anyString(), anyString());
        verify(referenceGenerator, times(2)).generateReferenceCharacter(6);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(transfertConfirmationService).sendTransactionConfirmation(any(TransfertConfirmationInfo.class));
    }


}
