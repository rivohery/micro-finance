package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.utils.MonthlyInterestCalculator;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.out.linker.HistoryTraceLinker;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddMonthlyInterestServiceApplicationTest {

    private static final ZoneId ZONE = ZoneId.of("Indian/Antananarivo");
    private static final Instant FIXED_INSTANT = Instant.parse("2024-01-15T07:00:00Z");
    private static final YearMonth JANUARY_2024 = YearMonth.of(2024, 1);

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private HistoryTraceLinker historyTraceLinker;

    @Mock
    private MonthlyInterestCalculator monthlyInterestCalculator;

    @Mock
    private Account account;

    @Mock
    private AccountNumber accountNumber;

    private AddMonthlyInterestServiceApplication service;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(FIXED_INSTANT, ZONE);
        service = new AddMonthlyInterestServiceApplication(
                transactionRepository,
                historyTraceLinker,
                monthlyInterestCalculator,
                fixedClock
        );
        when(account.getAccountNumber()).thenReturn(accountNumber);
    }

    // ------------------------------------------------------------------
    // Cas nominal
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Cas nominal")
    class NominalCase {

        @Test
        @DisplayName("Doit verifier l'orchestration des ports et d'agrégat dans le bon ordre")
        void shouldExecuteAddMonthlyInterestRate() {
            // Given
            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024))
                    .thenReturn(Collections.emptyList());
            when(monthlyInterestCalculator.calculate(account, Collections.emptyList(), JANUARY_2024))
                    .thenReturn(new BigDecimal("12.50"));

            // When
            service.execute(account);

            // Then
            verify(transactionRepository).findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024);
            verify(monthlyInterestCalculator).calculate(account, Collections.emptyList(), JANUARY_2024);
            verify(account).addMonthlyInterestRate(any(BigDecimal.class));
            verify(historyTraceLinker).saveMonthlyInterestRate(account, new BigDecimal("12.50"));
        }

        @Test
        @DisplayName("Doit passer les transactions et le mois courant au calculateur")
        void shouldPassTransactionsAndMonthToCalculator() {
            // Given
            Transaction tx1 = mock(Transaction.class);
            Transaction tx2 = mock(Transaction.class);
            List<Transaction> txs = List.of(tx1, tx2);

            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024))
                    .thenReturn(txs);
            when(monthlyInterestCalculator.calculate(account, txs, JANUARY_2024))
                    .thenReturn(new BigDecimal("10"));

            // When
            service.execute(account);

            // Then
            verify(monthlyInterestCalculator).calculate(account, txs, JANUARY_2024);
        }

        @Test
        @DisplayName("Doit créditer le compte avec les intérêts calculés")
        void shouldCreditAccountWithComputedInterest() {
            // Given
            BigDecimal interest = new BigDecimal("42.75");
            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024))
                    .thenReturn(Collections.emptyList());
            when(monthlyInterestCalculator.calculate(account, Collections.emptyList(), JANUARY_2024))
                    .thenReturn(interest);

            // When
            service.execute(account);

            // Then
            verify(account).addMonthlyInterestRate(interest);
        }

        @Test
        @DisplayName("Doit sauvegarder la trace historique avec les intérêts calculés")
        void shouldSaveHistoryTraceWithInterest() {
            // Given
            BigDecimal interest = new BigDecimal("42.75");
            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024))
                    .thenReturn(Collections.emptyList());
            when(monthlyInterestCalculator.calculate(account, Collections.emptyList(), JANUARY_2024))
                    .thenReturn(interest);

            // When
            service.execute(account);

            // Then
            verify(historyTraceLinker).saveMonthlyInterestRate(account, interest);
        }

        @Test
        @DisplayName("Doit retourner le compte passé en argument")
        void shouldReturnSameAccountInstance() {
            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024))
                    .thenReturn(Collections.emptyList());
            when(monthlyInterestCalculator.calculate(account, Collections.emptyList(), JANUARY_2024))
                    .thenReturn(new BigDecimal("10"));

            Account result = service.execute(account);

            assertThat(result).isSameAs(account);
        }
    }

    // ------------------------------------------------------------------
    // Ordre des opérations
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Séquencement des opérations")
    class Ordering {

        @Test
        @DisplayName("Doit créditer le compte AVANT de sauvegarder la trace historique")
        void shouldCreditAccountBeforeSavingHistory() {
            // Given
            BigDecimal interest = new BigDecimal("10");
            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024))
                    .thenReturn(Collections.emptyList());
            when(monthlyInterestCalculator.calculate(account, Collections.emptyList(), JANUARY_2024))
                    .thenReturn(interest);

            // When
            service.execute(account);

            // Then
            InOrder inOrder = inOrder(account, historyTraceLinker);
            inOrder.verify(account).addMonthlyInterestRate(interest);
            inOrder.verify(historyTraceLinker).saveMonthlyInterestRate(account, interest);
        }

        @Test
        @DisplayName("Doit récupérer les transactions AVANT de calculer les intérêts")
        void shouldFetchTransactionsBeforeCalculatingInterest() {
            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024))
                    .thenReturn(Collections.emptyList());
            when(monthlyInterestCalculator.calculate(any(), any(), any()))
                    .thenReturn(BigDecimal.ZERO);

            service.execute(account);

            InOrder inOrder = inOrder(transactionRepository, monthlyInterestCalculator);
            inOrder.verify(transactionRepository)
                    .findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024);
            inOrder.verify(monthlyInterestCalculator)
                    .calculate(eq(account), any(), eq(JANUARY_2024));
        }
    }

    // ------------------------------------------------------------------
    // Cas limites
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Cas limites")
    class EdgeCases {

        @Test
        @DisplayName("Doit gérer un calcul d'intérêts égal à zéro")
        void shouldHandleZeroInterest() {
            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024))
                    .thenReturn(Collections.emptyList());
            when(monthlyInterestCalculator.calculate(account, Collections.emptyList(), JANUARY_2024))
                    .thenReturn(BigDecimal.ZERO);

            service.execute(account);

            verify(account).addMonthlyInterestRate(BigDecimal.ZERO);
            verify(historyTraceLinker).saveMonthlyInterestRate(account, BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Doit gérer des intérêts négatifs (découvert)")
        void shouldHandleNegativeInterest() {
            BigDecimal negativeInterest = new BigDecimal("-5.25");
            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024))
                    .thenReturn(Collections.emptyList());
            when(monthlyInterestCalculator.calculate(account, Collections.emptyList(), JANUARY_2024))
                    .thenReturn(negativeInterest);

            service.execute(account);

            verify(account).addMonthlyInterestRate(negativeInterest);
            verify(historyTraceLinker).saveMonthlyInterestRate(account, negativeInterest);
        }
    }

    // ------------------------------------------------------------------
    // Robustesse du Clock
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Détermination du mois courant via Clock")
    class ClockBehavior {

        @Test
        @DisplayName("Doit utiliser le mois du Clock, pas le mois réel actuel")
        void shouldUseClockMonthNotRealCurrentMonth() {
            // Le Clock est fixé à janvier 2024, quel que soit le moment réel du test
            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024))
                    .thenReturn(Collections.emptyList());
            when(monthlyInterestCalculator.calculate(account, Collections.emptyList(), JANUARY_2024))
                    .thenReturn(BigDecimal.ZERO);

            service.execute(account);

            verify(transactionRepository)
                    .findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024);
            verify(transactionRepository, never())
                    .findMonthlyTransactionsOfAccount(eq(accountNumber), argThat(ym -> !ym.equals(JANUARY_2024)));
        }

        @Test
        @DisplayName("Doit gérer correctement le passage de mois (février)")
        void shouldHandleFebruary() {
            // Clock fixé au 1er février 2024 à 00:00 (heure Tana)
            Instant febInstant = Instant.parse("2024-01-31T21:00:00Z"); // 1er fév 00:00 Tana
            Clock febClock = Clock.fixed(febInstant, ZONE);
            AddMonthlyInterestServiceApplication febService = new AddMonthlyInterestServiceApplication(
                    transactionRepository,
                    historyTraceLinker,
                    monthlyInterestCalculator,
                    febClock
            );
            YearMonth february = YearMonth.of(2024, 2);

            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, february))
                    .thenReturn(Collections.emptyList());
            when(monthlyInterestCalculator.calculate(account, Collections.emptyList(), february))
                    .thenReturn(BigDecimal.ZERO);

            febService.execute(account);

            verify(transactionRepository)
                    .findMonthlyTransactionsOfAccount(accountNumber, february);
        }

        @Test
        @DisplayName("Doit gérer le passage d'année (décembre -> janvier)")
        void shouldHandleYearChange() {
            Instant decInstant = Instant.parse("2024-12-31T21:00:00Z"); // 1er janv 2025 Tana
            Clock decClock = Clock.fixed(decInstant, ZONE);
            AddMonthlyInterestServiceApplication decService = new AddMonthlyInterestServiceApplication(
                    transactionRepository,
                    historyTraceLinker,
                    monthlyInterestCalculator,
                    decClock
            );
            YearMonth january2025 = YearMonth.of(2025, 1);

            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, january2025))
                    .thenReturn(Collections.emptyList());
            when(monthlyInterestCalculator.calculate(account, Collections.emptyList(), january2025))
                    .thenReturn(BigDecimal.ZERO);

            decService.execute(account);

            verify(transactionRepository)
                    .findMonthlyTransactionsOfAccount(accountNumber, january2025);
        }
    }

    // ------------------------------------------------------------------
    // Propagation des exceptions
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Propagation des exceptions")
    class ExceptionPropagation {

        @Test
        @DisplayName("Doit propager l'exception du repository et ne rien créditer")
        void shouldPropagateRepositoryException() {
            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024))
                    .thenThrow(new RuntimeException("DB down"));

            assertThatThrownBy(() -> service.execute(account))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("DB down");

            verifyNoInteractions(monthlyInterestCalculator);
            verify(account, never()).addMonthlyInterestRate(any());
            verifyNoInteractions(historyTraceLinker);
        }

        @Test
        @DisplayName("Doit propager l'exception du calculateur et ne rien créditer")
        void shouldPropagateCalculatorException() {
            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024))
                    .thenReturn(Collections.emptyList());
            when(monthlyInterestCalculator.calculate(any(), any(), any()))
                    .thenThrow(new IllegalStateException("bad calculation"));

            assertThatThrownBy(() -> service.execute(account))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("bad calculation");

            verify(account, never()).addMonthlyInterestRate(any());
            verifyNoInteractions(historyTraceLinker);
        }

        @Test
        @DisplayName("Doit propager l'exception du HistoryTraceLinker")
        void shouldPropagateHistoryLinkerException() {
            BigDecimal interest = new BigDecimal("10");
            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024))
                    .thenReturn(Collections.emptyList());
            when(monthlyInterestCalculator.calculate(any(), any(), any()))
                    .thenReturn(interest);
            doThrow(new RuntimeException("trace KO"))
                    .when(historyTraceLinker).saveMonthlyInterestRate(account, interest);

            assertThatThrownBy(() -> service.execute(account))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("trace KO");

            // ⚠️ Le compte a déjà été crédité -> l'absence de transaction/rollback
            // est un point d'attention (voir commentaire dans le code)
            verify(account).addMonthlyInterestRate(interest);
        }
    }

    // ------------------------------------------------------------------
    // Capture d'arguments (documentation du contrat)
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Contrat des arguments passés")
    class ArgumentContract {

        @Test
        @DisplayName("L'AccountNumber passé au repository doit être celui du compte")
        void shouldPassAccountNumberFromAccount() {
            AccountNumber expected = mock(AccountNumber.class);
            when(account.getAccountNumber()).thenReturn(expected);
            when(transactionRepository.findMonthlyTransactionsOfAccount(expected, JANUARY_2024))
                    .thenReturn(Collections.emptyList());
            when(monthlyInterestCalculator.calculate(any(), any(), any()))
                    .thenReturn(BigDecimal.ZERO);

            service.execute(account);

            ArgumentCaptor<AccountNumber> captor = ArgumentCaptor.forClass(AccountNumber.class);
            verify(transactionRepository).findMonthlyTransactionsOfAccount(captor.capture(), eq(JANUARY_2024));
            assertThat(captor.getValue()).isSameAs(expected);
        }

        @Test
        @DisplayName("Les intérêts passés au linker et au compte doivent être identiques")
        void shouldPassSameInterestAmountToAccountAndLinker() {
            BigDecimal interest = new BigDecimal("99.99");
            when(transactionRepository.findMonthlyTransactionsOfAccount(accountNumber, JANUARY_2024))
                    .thenReturn(Collections.emptyList());
            when(monthlyInterestCalculator.calculate(any(), any(), any()))
                    .thenReturn(interest);

            service.execute(account);

            ArgumentCaptor<BigDecimal> accountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
            ArgumentCaptor<BigDecimal> linkerCaptor = ArgumentCaptor.forClass(BigDecimal.class);

            verify(account).addMonthlyInterestRate(accountCaptor.capture());
            verify(historyTraceLinker).saveMonthlyInterestRate(eq(account), linkerCaptor.capture());

            assertThat(accountCaptor.getValue()).isEqualByComparingTo(linkerCaptor.getValue());
        }
    }
}
