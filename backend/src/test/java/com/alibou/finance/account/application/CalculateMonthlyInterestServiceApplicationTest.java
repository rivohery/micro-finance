package com.alibou.finance.account.application;

import com.alibou.finance.log.application.port.usecase.InterestRateUseCase;
import com.alibou.finance.account.application.service.CalculateMonthlyInterestServiceApplication;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.log.domain.agregate.InterestRateTrace;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.account.domain.vo.Balance;
import com.alibou.finance.account.domain.vo.MgaBalance;
import com.alibou.finance.log.domain.vo.transaction.SoldBeforeTransaction;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.log.domain.vo.accountStatusHistory.InterestRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalculateMonthlyInterestServiceApplicationTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CurrencyExchangePort currencyExchangePort;
    @Mock
    private InterestRateUseCase interestRateUseCase;
    @InjectMocks
    private CalculateMonthlyInterestServiceApplication calculateMonthlyInterestService;

    Account account;

    @BeforeEach
    void setUp(){
        AccountType accountType = AccountType.builder().annualInterestRate(new InterestRate(BigDecimal.valueOf(0.2))).build();
        Currency currency = Currency.builder().code(new CurrencyCode("MGA")).build();
        account = Account.builder()
                .accountType(accountType)
                .accountNumber(new AccountNumber("012-20-0123456789"))
                .balance(new Balance(BigDecimal.valueOf(1000)))
                .mgaBalance(new MgaBalance(BigDecimal.valueOf(1000)))
                .currency(currency)
                .build();
    }

    @Test
    void shouldCalculNbrDaysBetweenTest(){
        LocalDateTime startTime = LocalDateTime.of(2026,6,10,0,0,0);
        LocalDateTime endTime = LocalDateTime.of(2026,6,17,0,0,0);
        long nbrDays =  calculateMonthlyInterestService.calculNbrDaysBetween(startTime, endTime);
        assertThat(nbrDays).isEqualTo(7);
    }

    @Test
    void shouldCalculateInterestRateOfSpecificDaysWithSuccess(){
        BigDecimal potentialSold = BigDecimal.valueOf(400);
        LocalDateTime startTime = LocalDateTime.of(2026,6,10,0,0,0);
        LocalDateTime endTime = LocalDateTime.of(2026,6,17,0,0,0);
        BigDecimal interestRateOfSpecificDaysResult = calculateMonthlyInterestService.calculInterestRateOfSpecificDays(account, potentialSold, startTime, endTime);

        //calcule du taux intérêt susceptible
        BigDecimal daylyInterestRate = BigDecimal.valueOf(0.2).divide(BigDecimal.valueOf(365),10,RoundingMode.HALF_UP).multiply(potentialSold);
        BigDecimal interestRateOfSpecificDaysExpected = BigDecimal.valueOf(7 * (daylyInterestRate.doubleValue())).setScale(10, RoundingMode.HALF_UP);

        assertThat(interestRateOfSpecificDaysResult.compareTo(interestRateOfSpecificDaysExpected)).isEqualTo(0);
    }

    @Test
    @DisplayName("Devrait exécuter la méthode principale de ce UseCase :{execute method}")
    void shouldExecuteCalculateMonthlyInterestRate(){
        LocalDate now = LocalDate.now();
        Transaction tr1 = Transaction.builder()
                .accountNumber(account.getAccountNumber())
                .soldBeforeTransaction(new SoldBeforeTransaction(BigDecimal.valueOf(1000)))
                .createdDate(LocalDateTime.of(2026,now.getMonthValue(),7,0,0,0))
                .build();
        Transaction tr2 = Transaction.builder()
                .accountNumber(account.getAccountNumber())
                .soldBeforeTransaction(new SoldBeforeTransaction(BigDecimal.valueOf(400)))
                .createdDate(LocalDateTime.of(2026,now.getMonthValue(),17,0,0,0))
                .build();
        Transaction tr3 = Transaction.builder()
                .accountNumber(account.getAccountNumber())
                .soldBeforeTransaction(new SoldBeforeTransaction(BigDecimal.valueOf(500)))
                .createdDate(LocalDateTime.of(2026,now.getMonthValue(),27,0,0,0))
                .build();
        List<Transaction>transactionsOfAccount = List.of(tr1,tr2,tr3).stream().sorted(Comparator.comparing(Transaction::getCreatedDate)).toList();

        when(transactionRepository.checkMonthlyTransactionOfAccount(
                any(AccountNumber.class), any(LocalDateTime.class), any(LocalDateTime.class))
        ).thenReturn(transactionsOfAccount);
        when(currencyExchangePort.getExchangeRate(anyString(), anyString())).thenReturn(BigDecimal.ONE);
        when(interestRateUseCase.save(any(InterestRateTrace.class))).thenAnswer(i -> i.getArgument(0));

        Account accountExpected = calculateMonthlyInterestService.execute(account);

        System.out.println(accountExpected.getBalance().value());
        System.out.println(accountExpected.getMgaBalance().value());

        verify(transactionRepository, times(1)).checkMonthlyTransactionOfAccount(any(AccountNumber.class), any(), any());
        verify(currencyExchangePort, times(1)).getExchangeRate(any(), any());
        verify(interestRateUseCase).save(any(InterestRateTrace.class));

    }
}

