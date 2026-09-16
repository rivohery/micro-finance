package com.alibou.finance.account.application;

import com.alibou.finance.account.domain.exception.ThirdPartyServiceException;
import com.alibou.finance.log.application.port.usecase.InterestRateUseCase;
import com.alibou.finance.account.application.service.AddMonthlyInterestServiceApplication;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.log.domain.agregate.InterestRateTrace;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.account.domain.vo.Balance;
import com.alibou.finance.account.domain.vo.MgaBalance;
import com.alibou.finance.log.domain.out.service.InterestRateTraceFactory;
import com.alibou.finance.log.domain.vo.transaction.SoldBeforeTransaction;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.log.domain.vo.accountStatusHistory.InterestRate;
import com.alibou.finance.shared.domain.IllegalArgumentException;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddMonthlyInterestServiceApplicationTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CurrencyExchangePort currencyExchangePort;
    @Mock
    private InterestRateUseCase interestRateUseCase;
    @Mock
    private InterestRateTraceFactory interestRateTraceFactory;
    @InjectMocks
    private AddMonthlyInterestServiceApplication addMonthlyInterestService;

    private AccountType accountType;
    private Currency currency;
    private Account account;
    private LocalDate today;
    private LocalDateTime startOfMonth;
    private LocalDateTime endOfMonth;

    private InterestRateTrace fakeTrace = mock(InterestRateTrace.class);

    @BeforeEach
    void setUp(){
        accountType = AccountType.builder().annualInterestRate(new InterestRate(BigDecimal.valueOf(3.65))).build();//=> dailyRate: 0.0001
        currency = Currency.builder().code(new CurrencyCode("MGA")).build();

        today = LocalDate.now();
        startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth()).atTime(LocalTime.MIN);
        endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

    }

    @Test
    void shouldCalculNbrDaysBetweenTest(){
        LocalDateTime startTime = LocalDateTime.of(2026,6,10,0,0,0);
        LocalDateTime endTime = LocalDateTime.of(2026,6,17,0,0,0);
        long nbrDays =  addMonthlyInterestService.calculNbrDaysBetween(startTime, endTime);
        assertThat(nbrDays).isEqualTo(7);
    }

    @Test
    void shouldCalculateInterestRateOfSpecificDaysWithSuccess(){
        // Compte avec un solde de 1 000 000 MGA
        account = Account.builder()
                .accountNumber(new AccountNumber("056-10-0123456789"))
                .accountType(accountType)
                .currency(currency)
                .balance(new Balance(new BigDecimal("1000000"))) //Solde à la fin du mois
                .mgaBalance(new MgaBalance(new BigDecimal("1000000")))
                .build();

        BigDecimal potentialSold = BigDecimal.valueOf(400);
        LocalDateTime startTime = LocalDateTime.of(2026,6,10,0,0,0);
        LocalDateTime endTime = LocalDateTime.of(2026,6,17,0,0,0);
        BigDecimal result = addMonthlyInterestService.calculInterestRateOfSpecificDays(account, potentialSold, startTime, endTime);

        //calcule du taux intérêt susceptible
        BigDecimal dailyInterestRateExpected = new BigDecimal("" + 0.0001);
        BigDecimal specificDaysInterestRateOfPotentialSoldExpected = potentialSold.multiply(dailyInterestRateExpected).multiply(new BigDecimal("7"));

        assertThat(result).isEqualByComparingTo(specificDaysInterestRateOfPotentialSoldExpected);
    }

    // =====================================================================
    // CAS SUCCÈS : plusieurs transactions dans le mois
    // =====================================================================
    @Test
    @DisplayName("execute - cas succès avec plusieurs transactions : calcul de l'intérêt segmenté")
    void execute_shouldCalculateInterestForEachSegmentBetweenTransactions() {
        //GIVEN
        // Compte avec un solde de 1 000 000 MGA
        account = Account.builder()
                .accountNumber(new AccountNumber("056-10-0123456789"))
                .accountType(accountType)
                .currency(currency)
                .balance(new Balance(new BigDecimal("1000000"))) //Solde à la fin du mois
                .mgaBalance(new MgaBalance(new BigDecimal("1000000")))
                .build();

        // Taux d'intérêt journalier simulé pour le compte : 0.0001 (soit 3.65%/an)
        BigDecimal dailyRate = new BigDecimal("0.0001");
        BigDecimal actualBalance = new BigDecimal("1000000");

        // 3 transactions dans le mois
        LocalDateTime tx1Date = startOfMonth.plusDays(5);   // 06 du mois
        LocalDateTime tx2Date = startOfMonth.plusDays(15);  // 16 du mois
        LocalDateTime tx3Date = startOfMonth.plusDays(25);  // 26 du mois

        Transaction tx1 = mockTransaction(tx1Date, new BigDecimal("800000"));   // solde avant tx1
        Transaction tx2 = mockTransaction(tx2Date, new BigDecimal("900000"));   // solde avant tx2
        Transaction tx3 = mockTransaction(tx3Date, new BigDecimal("950000"));   // solde avant tx3

        List<Transaction> transactions = List.of(tx1, tx2, tx3);

        when(transactionRepository.checkMonthlyTransactionOfAccount(eq(account.getAccountNumber()), eq(startOfMonth), eq(endOfMonth)))
                .thenReturn(transactions);

        // Taux de change MGA
        BigDecimal mgaExchangeRate = new BigDecimal("1.0");
        when(currencyExchangePort.getExchangeRate(anyString(), eq("MGA"))).thenReturn(mgaExchangeRate);

        when(interestRateTraceFactory.prepare(any(Account.class), any(BigDecimal.class), any(BigDecimal.class))).thenReturn(fakeTrace);

        // WHEN
        Account result = addMonthlyInterestService.execute(account);


        // THEN
        // Segment 1 : du 1er du mois (startOfMonth) à tx1Date
        long daysSeg1 = java.time.temporal.ChronoUnit.DAYS.between(startOfMonth, tx1Date); // 5
        BigDecimal expectedSeg1 = new BigDecimal("800000").multiply(dailyRate).multiply(BigDecimal.valueOf(daysSeg1));

        // Segment 2 : de tx1Date à tx2Date, sur soldBeforeTransaction de tx2 (900 000)
        long daysSeg2 = java.time.temporal.ChronoUnit.DAYS.between(tx1Date, tx2Date); // 10
        BigDecimal expectedSeg2 = new BigDecimal("900000").multiply(dailyRate).multiply(BigDecimal.valueOf(daysSeg2));

        // Segment 3 : de tx2Date à tx3Date, sur soldBeforeTransaction de tx3 (950 000)
        long daysSeg3 = java.time.temporal.ChronoUnit.DAYS.between(tx2Date, tx3Date); // 10
        BigDecimal expectedSeg3 = new BigDecimal("950000").multiply(dailyRate).multiply(BigDecimal.valueOf(daysSeg3));

        // Segment 4 : de tx3Date à endOfMonth, sur le solde actuel du compte (1 000 000)
        long daysSeg4 = java.time.temporal.ChronoUnit.DAYS.between(tx3Date, endOfMonth);
        BigDecimal expectedSeg4 = actualBalance.multiply(dailyRate).multiply(BigDecimal.valueOf(daysSeg4));

        BigDecimal expectedTotal = expectedSeg1.add(expectedSeg2).add(expectedSeg3).add(expectedSeg4);
        BigDecimal expectedBalance = actualBalance.add(expectedTotal);

        // Vérifie que l'objet Account retourné est bien celui passé en paramètre (modifié)
        assertThat(result).isSameAs(account);
        assertThat(result.getBalance().value())
                .as("Le solde après l'ajout du taux d'intérêts mensuel")
                .isEqualByComparingTo(expectedBalance);
        assertThat(result.getMgaBalance().value())
                .as("Le solde en MGA après l'ajout du taux d'intérêts mensuel")
                .isEqualByComparingTo(expectedBalance.multiply(mgaExchangeRate));


        // Vérification que le taux de change a été demandé
        verify(currencyExchangePort).getExchangeRate(anyString(), eq("MGA"));

        verify(interestRateTraceFactory).prepare(any(Account.class), any(BigDecimal.class), any(BigDecimal.class));

        // Vérification que la trace a été persistée
        ArgumentCaptor<InterestRateTrace> traceCaptor = ArgumentCaptor.forClass(InterestRateTrace.class);
        verify(interestRateUseCase).save(traceCaptor.capture());

        InterestRateTrace savedTrace = traceCaptor.getValue();
        assertThat(savedTrace).isNotNull();
    }

    @Test
    @DisplayName("execute - cas succès sans transaction : intérêt calculé sur tout le mois avec le solde actuel")
    void execute_shouldCalculateInterestForWholeMonthWhenNoTransaction() {
        // ----- Given -----
        BigDecimal balance = new BigDecimal("500000");
        BigDecimal dailyRate = new BigDecimal("0.0002");

        account = mock(Account.class);
        Currency eurCurrency = Currency.builder().code(new CurrencyCode("EUR")).build();
        when(account.getAccountNumber()).thenReturn(new AccountNumber("056-10-0123456789"));
        when(account.getBalance()).thenReturn(new Balance(balance));
        when(account.getCurrency()).thenReturn(eurCurrency);
        when(account.calculateInterestRateForSpecificDays(any(BigDecimal.class), any(Long.class))).thenAnswer(i -> {
            BigDecimal sold = i.getArgument(0);
            Long nbrDays = i.getArgument(1);
            return sold.multiply(dailyRate).multiply(new BigDecimal("" + nbrDays));
        });

        when(transactionRepository.checkMonthlyTransactionOfAccount(any(AccountNumber.class), eq(startOfMonth), eq(endOfMonth)))
                .thenReturn(Collections.emptyList());
        BigDecimal mgaExchangeRate = new BigDecimal("5000");
        when(currencyExchangePort.getExchangeRate(eq("EUR"), eq("MGA"))).thenReturn(mgaExchangeRate);
        when(interestRateTraceFactory.prepare(any(Account.class), any(BigDecimal.class), any(BigDecimal.class))).thenReturn(fakeTrace);

        // ----- When -----
        Account result = addMonthlyInterestService.execute(account);

        // ----- Then -----
        long daysInMonth = java.time.temporal.ChronoUnit.DAYS.between(startOfMonth, endOfMonth);
        BigDecimal expectedInterest = balance.multiply(dailyRate).multiply(BigDecimal.valueOf(daysInMonth));

        ArgumentCaptor<BigDecimal> interestCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(account).addMonthlyInterestRate(interestCaptor.capture());

        assertThat(interestCaptor.getValue())
                .as("Intérêt total = solde * taux journalier * nombre de jours du mois")
                .isEqualByComparingTo(expectedInterest);

        // Un seul appel au calcul segmenté
        verify(account, times(1)).calculateInterestRateForSpecificDays(any(BigDecimal.class), anyLong());

        verify(account).calculMgaBalance(mgaExchangeRate);

        verify(interestRateTraceFactory).prepare(any(Account.class), any(BigDecimal.class), any(BigDecimal.class));

        verify(interestRateUseCase).save(any(InterestRateTrace.class));
        assertThat(result).isSameAs(account);
    }

    @Test
    @DisplayName("execute - doit lever une exception NullPointerException si l'un des paramètres dans interestRateTraceFactory.prepare est nul")
    void execute_shouldHandleNullPointerException(){
        //GIVEN
        BigDecimal balance = new BigDecimal("1000000");
        BigDecimal dailyRate = new BigDecimal("0.001");
        BigDecimal exchangeRate = new BigDecimal("4500");

        account = mock(Account.class);
        Currency eurCurrency = Currency.builder().code(new CurrencyCode("EUR")).build();
        when(account.getAccountNumber()).thenReturn(new AccountNumber("056-10-0123456789"));
        when(account.getBalance()).thenReturn(new Balance(balance));
        when(account.getCurrency()).thenReturn(eurCurrency);
        when(transactionRepository.checkMonthlyTransactionOfAccount(any(AccountNumber.class), eq(startOfMonth), eq(endOfMonth)))
                .thenReturn(Collections.emptyList());
        when(account.calculateInterestRateForSpecificDays(any(BigDecimal.class), any(Long.class))).thenAnswer(i -> {
            BigDecimal sold = i.getArgument(0);
            Long nbrDays = i.getArgument(1);
            return sold.multiply(dailyRate).multiply(new BigDecimal("" + nbrDays));
        });
        when(currencyExchangePort.getExchangeRate(eq("EUR"), eq("MGA"))).thenReturn(exchangeRate);

        doThrow(new NullPointerException("Some parameter is null"))
                .when(interestRateTraceFactory)
                .prepare(any(Account.class), any(BigDecimal.class), any(BigDecimal.class));

        assertThatThrownBy(() -> addMonthlyInterestService.execute(account))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class)
                .hasMessage("Some parameter is null");

        verify(account).addMonthlyInterestRate(any(BigDecimal.class));
        verify(currencyExchangePort).getExchangeRate(anyString(), anyString());
        verify(account).calculMgaBalance(any(BigDecimal.class));
        verify(interestRateUseCase, never()).save(any(InterestRateTrace.class));

    }


    @Test
    @DisplayName("execute - doit lever une exception ThirdPartyServiceException si la connexion avec API externe pour le taux d'échange est interrompu")
    void execute_shouldHandleThirdPartyServiceException(){
        //GIVEN
        BigDecimal balance = new BigDecimal("1000000");
        BigDecimal dailyRate = new BigDecimal("0.001");
        BigDecimal exchangeRate = new BigDecimal("4500");

        account = mock(Account.class);
        Currency eurCurrency = Currency.builder().code(new CurrencyCode("EUR")).build();
        when(account.getAccountNumber()).thenReturn(new AccountNumber("056-10-0123456789"));
        when(account.getBalance()).thenReturn(new Balance(balance));
        when(account.getCurrency()).thenReturn(eurCurrency);
        when(transactionRepository.checkMonthlyTransactionOfAccount(any(AccountNumber.class), eq(startOfMonth), eq(endOfMonth)))
                .thenReturn(Collections.emptyList());
        when(account.calculateInterestRateForSpecificDays(any(BigDecimal.class), any(Long.class))).thenAnswer(i -> {
            BigDecimal sold = i.getArgument(0);
            Long nbrDays = i.getArgument(1);
            return sold.multiply(dailyRate).multiply(new BigDecimal("" + nbrDays));
        });

        doThrow(new ThirdPartyServiceException("Le service de conversion monétaire est temporairement indisponible. Veuillez réessayer plus tard."))
                .when(currencyExchangePort)
                .getExchangeRate(eq("EUR"), eq("MGA"));

        assertThatThrownBy(() -> addMonthlyInterestService.execute(account))
                .isInstanceOf(ThirdPartyServiceException.class)
                .hasMessage("Le service de conversion monétaire est temporairement indisponible. Veuillez réessayer plus tard.");

        verify(account).addMonthlyInterestRate(any(BigDecimal.class));
        verify(currencyExchangePort).getExchangeRate(anyString(), anyString());
        verify(account, never()).calculMgaBalance(any(BigDecimal.class));
        verify(interestRateUseCase, never()).save(any(InterestRateTrace.class));

    }

    private Transaction mockTransaction(LocalDateTime createdDate, BigDecimal soldBeforeTransaction) {
        Transaction tx = mock(Transaction.class);
        when(tx.getCreatedDate()).thenReturn(createdDate);
        when(tx.getSoldBeforeTransaction()).thenReturn(new SoldBeforeTransaction(soldBeforeTransaction));
        return tx;
    }

}


