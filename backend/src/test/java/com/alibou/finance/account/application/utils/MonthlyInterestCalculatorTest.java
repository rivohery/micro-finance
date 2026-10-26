package com.alibou.finance.account.application.utils;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.vo.account.Balance;
import com.alibou.finance.account.domain.vo.transaction.SoldBeforeTransaction;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonthlyInterestCalculatorTest {

    private static final InterestRate RATE_10_PERCENT = new InterestRate(new BigDecimal("10"));
    private static final InterestRate RATE_365_PERCENT = new InterestRate(new BigDecimal("365"));
    private static final YearMonth JANUARY_2024 = YearMonth.of(2024, 1); // 31 jours

    private MonthlyInterestCalculator calculator;

    @Mock
    private Account account;

    @Mock
    private Balance balance;

    @BeforeEach
    void setUp() {
        calculator = new MonthlyInterestCalculator();
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Configure le mock Account pour que calculateInterestAmount applique
     * la vraie formule métier : solde * (taux/100/365) * jours.
     * Cela évite de dupliquer la formule et garde la cohérence.
     */
    private void stubInterestCalculationWithRate(BigDecimal annualRatePercent) {
        when(account.calculateInterestAmount(any(), anyLong()))
                .thenAnswer(invocation -> {
                    BigDecimal sold = invocation.getArgument(0);
                    long days = invocation.getArgument(1);
                    if (sold == null || days < 0) {
                        throw new IllegalArgumentException("bad input");
                    }
                    if (days == 0 || sold.compareTo(BigDecimal.ZERO) == 0) {
                        return BigDecimal.ZERO;
                    }
                    BigDecimal daily = annualRatePercent
                            .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                            .divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);
                    return sold.multiply(daily).multiply(BigDecimal.valueOf(days));
                });
    }

    private void stubCurrentBalance(BigDecimal value) {
        when(account.getBalance()).thenReturn(balance);
        when(balance.value()).thenReturn(value);
    }

    private Transaction mockTransaction(LocalDateTime date, BigDecimal soldBefore) {
        Transaction tx = mock(Transaction.class);
        when(tx.getCreatedDate()).thenReturn(date);
        SoldBeforeTransaction soldBeforeTransaction= mock(SoldBeforeTransaction.class);
        when(tx.getSoldBeforeTransaction()).thenReturn(soldBeforeTransaction);
        when(soldBeforeTransaction.value()).thenReturn(soldBefore);
        return tx;
    }

    // ------------------------------------------------------------------
    // Cas : aucune transaction
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Aucune transaction dans le mois")
    class NoTransactions {

        @Test
        @DisplayName("Doit calculer les intérêts sur tout le mois (mois: 31 jours => taux intérêts calculer sur 30 jours) avec le solde courant")
        void shouldCalculateOnFullMonthWithCurrentBalance() {
            // Given
            stubCurrentBalance(new BigDecimal("1000"));
            stubInterestCalculationWithRate(new BigDecimal("10"));

            // When
            BigDecimal result = calculator.calculate(account, Collections.emptyList(), JANUARY_2024);

            // Then : 1000 * (10% / 365) * 31 = 8.2191780000...
            BigDecimal dailyInterestRate = new BigDecimal("10")
                    .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                    .divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);
            BigDecimal expected = new BigDecimal("1000")
                    .multiply(dailyInterestRate)
                    .multiply(BigDecimal.valueOf(30));
            assertThat(result).isEqualByComparingTo(expected);
        }

        @Test
        @DisplayName("Doit retourner zéro si le solde est nul")
        void shouldReturnZeroWhenBalanceIsZero() {
            stubCurrentBalance(BigDecimal.ZERO);
            stubInterestCalculationWithRate(new BigDecimal("10"));

            BigDecimal result = calculator.calculate(account, Collections.emptyList(), JANUARY_2024);

            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Doit retourner zéro si le taux est nul")
        void shouldReturnZeroWhenRateIsZero() {
            stubCurrentBalance(new BigDecimal("5000"));
            stubInterestCalculationWithRate(BigDecimal.ZERO);

            BigDecimal result = calculator.calculate(account, Collections.emptyList(), JANUARY_2024);

            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Doit gérer un mois de 28 jours pour le mois février bissextile)")//1 jour exclu, dernière inclue
        void shouldHandleLeapYearFebruary() {
            YearMonth feb2024 = YearMonth.of(2024, 2); // 29 jours
            stubCurrentBalance(new BigDecimal("36500"));
            stubInterestCalculationWithRate(new BigDecimal("100"));

            BigDecimal result = calculator.calculate(account, Collections.emptyList(), feb2024);

            // 36500 * (100% / 365) * 28 = 36500 * (1 / 365) * 28 = 100 * 28 = 2800 , avec BigDecimal(arrondie) => 2899.9999720000
            assertThat(result).isEqualByComparingTo(new BigDecimal("2799.9999720000"));
        }
    }

    // ------------------------------------------------------------------
    // Cas : une seule transaction
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Une seule transaction")
    class SingleTransaction {

        @Test
        @DisplayName("Doit découper la période en 2 et appliquer les bons soldes")
        void shouldSplitPeriodInTwo() {
            // Given : solde initial 1000, dépôt le 16/01 -> solde après = 2000
            LocalDateTime txDate = LocalDateTime.of(2024, 1, 16, 10, 0);
            Transaction tx = mockTransaction(txDate, new BigDecimal("1000"));

            stubCurrentBalance(new BigDecimal("2000"));
            stubInterestCalculationWithRate(new BigDecimal("10"));

            // When
            BigDecimal result = calculator.calculate(account, List.of(tx), JANUARY_2024);

            // Then
            // Période 1 : 01/01 -> 16/01 = 15 jours (inclusif) sur 1000
            // Période 2 : 16/01 -> 31/01 = 15 jours (inclusif) sur 2000
            BigDecimal dailyRate = new BigDecimal("10")
                    .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                    .divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);

            BigDecimal expected = new BigDecimal("1000").multiply(dailyRate).multiply(BigDecimal.valueOf(15))
                    .add(new BigDecimal("2000").multiply(dailyRate).multiply(BigDecimal.valueOf(15)));

            assertThat(result).isEqualByComparingTo(expected);
        }

        @Test
        @DisplayName("Transaction le 1er jour du mois : période 1 = 1 jour")
        void shouldHandleTransactionOnFirstDayOfMonth() {
            LocalDateTime txDate = LocalDateTime.of(2024, 1, 1, 0, 0);
            Transaction tx = mockTransaction(txDate, new BigDecimal("0"));

            stubCurrentBalance(new BigDecimal("1000"));
            stubInterestCalculationWithRate(new BigDecimal("10"));

            BigDecimal result = calculator.calculate(account, List.of(tx), JANUARY_2024);

            BigDecimal dailyRate = new BigDecimal("10")
                    .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                    .divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);

            // Période 1 : solde 0 sur 1 jour = 0
            // Période 2 : solde 1000 sur 30 jours (01/01 exclus -> 31/01 inclus)
            BigDecimal expected = new BigDecimal("1000").multiply(dailyRate).multiply(BigDecimal.valueOf(30));

            assertThat(result).isEqualByComparingTo(expected);
        }

        @Test
        @DisplayName("Transaction le dernier jour du mois : période 2 = 1 jour")
        void shouldHandleTransactionOnLastDayOfMonth() {
            LocalDateTime txDate = LocalDateTime.of(2024, 1, 31, 23, 59);
            Transaction tx = mockTransaction(txDate, new BigDecimal("1000"));

            stubCurrentBalance(new BigDecimal("500"));
            stubInterestCalculationWithRate(new BigDecimal("10"));

            BigDecimal result = calculator.calculate(account, List.of(tx), JANUARY_2024);

            BigDecimal dailyRate = new BigDecimal("10")
                    .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                    .divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);

            // Période 1 : 01/01 -> 31/01 inclusif = 31 jours sur 1000
            // Période 2 : 31/01 -> 31/01 inclusif = 1 jour sur 500
            BigDecimal expected = new BigDecimal("1000").multiply(dailyRate).multiply(BigDecimal.valueOf(30))
                    .add(new BigDecimal("500").multiply(dailyRate).multiply(BigDecimal.valueOf(0)));

            assertThat(result).isEqualByComparingTo(expected);
        }

        @Test
        @DisplayName("Solde négatif (découvert) : les intérêts doivent être négatifs")
        void shouldHandleNegativeBalance() {
            LocalDateTime txDate = LocalDateTime.of(2024, 1, 15, 12, 0);
            Transaction tx = mockTransaction(txDate, new BigDecimal("-500"));

            stubCurrentBalance(new BigDecimal("-500"));
            stubInterestCalculationWithRate(new BigDecimal("10"));

            BigDecimal result = calculator.calculate(account, List.of(tx), JANUARY_2024);

            assertThat(result).isNegative();
        }
    }

    // ------------------------------------------------------------------
    // Cas : plusieurs transactions
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Plusieurs transactions")
    class MultipleTransactions {

        @Test
        @DisplayName("Doit trier les transactions par date même si la liste est en désordre")
        void shouldSortTransactionsByDate() {
            // Transactions fournies en désordre
            Transaction tx3 = mockTransaction(LocalDateTime.of(2024, 1, 25, 8, 0), new BigDecimal("3000"));
            Transaction tx1 = mockTransaction(LocalDateTime.of(2024, 1, 10, 8, 0), new BigDecimal("1000"));
            Transaction tx2 = mockTransaction(LocalDateTime.of(2024, 1, 18, 8, 0), new BigDecimal("2000"));

            stubCurrentBalance(new BigDecimal("4000"));
            stubInterestCalculationWithRate(new BigDecimal("10"));

            // On appelle avec une liste non triée
            BigDecimal result = calculator.calculate(account, List.of(tx3, tx1, tx2), JANUARY_2024);

            // On refait le calcul avec une liste triée pour comparer
            BigDecimal expected = calculator.calculate(account, List.of(tx1, tx2, tx3), JANUARY_2024);

            assertThat(result).isEqualByComparingTo(expected);
        }

        @Test
        @DisplayName("Doit calculer correctement sur plusieurs segments")
        void shouldCalculateOnMultipleSegments() {
            // Scénario :
            // 01/01 -> 10/01 : solde 1000
            // 10/01 -> 20/01 : solde 1500
            // 20/01 -> 31/01 : solde 500 (après retrait, solde final = 500)
            Transaction tx1 = mockTransaction(LocalDateTime.of(2024, 1, 10, 12, 0), new BigDecimal("1000"));
            Transaction tx2 = mockTransaction(LocalDateTime.of(2024, 1, 20, 12, 0), new BigDecimal("1500"));

            stubCurrentBalance(new BigDecimal("500"));
            stubInterestCalculationWithRate(new BigDecimal("365")); // taux 365% -> daily = 1%

            BigDecimal result = calculator.calculate(account, List.of(tx1, tx2), JANUARY_2024);

            // daily rate = 365/100/365 = 0.01 (1%)
            BigDecimal daily = new BigDecimal("0.01");
            // Segment 1 : 01/01 -> 10/01 inclusif = 9 jours * 1000 * 0.01 = 90
            // Segment 2 : 10/01 -> 20/01 inclusif = 10 jours * 1500 * 0.01 = 150
            // Segment 3 : 20/01 -> 31/01 inclusif = 11 jours * 500 * 0.01 = 55
            BigDecimal expected = new BigDecimal("90")
                    .add(new BigDecimal("150"))
                    .add(new BigDecimal("55"));

            assertThat(result).isEqualByComparingTo(expected);
        }

        @Test
        @DisplayName("Deux transactions le même jour")
        void shouldHandleSameDayTransactions() {
            LocalDateTime date = LocalDateTime.of(2024, 1, 15, 12, 0);
            Transaction tx1 = mockTransaction(date, new BigDecimal("1000"));  //avant le 15, avant tx1 => taux intérêts positif
            Transaction tx2 = mockTransaction(date, new BigDecimal("1500")); //entre tx1 et tx2, même jour => taux intérêts null

            stubCurrentBalance(new BigDecimal("2000"));
            stubInterestCalculationWithRate(new BigDecimal("10"));

            // Doit ne pas planter et retourner un résultat cohérent
            BigDecimal result = calculator.calculate(account, List.of(tx1, tx2), JANUARY_2024);

            BigDecimal dailyRate = new BigDecimal("10")
                    .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                    .divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);
            BigDecimal expected = new BigDecimal("1000").multiply(dailyRate).multiply(BigDecimal.valueOf(14))
                    .add(new BigDecimal("1500").multiply(dailyRate).multiply(BigDecimal.valueOf(0)))
                    .add(new BigDecimal("2000").multiply(dailyRate).multiply(BigDecimal.valueOf(16)));

            assertThat(result).isNotNull();
            assertThat(result).isPositive();
            assertThat(result).isEqualByComparingTo(expected);
        }
    }

    // ------------------------------------------------------------------
    // Cas : robustesse / edge cases
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Robustesse")
    class Robustness {

        @Test
        @DisplayName("Doit retourner zéro si le solde et les transactions sont nuls partout")
        void shouldReturnZeroWhenEverythingIsZero() {
            Transaction tx = mockTransaction(LocalDateTime.of(2024, 1, 15, 0, 0), BigDecimal.ZERO);
            stubCurrentBalance(BigDecimal.ZERO);
            stubInterestCalculationWithRate(new BigDecimal("10"));

            BigDecimal result = calculator.calculate(account, List.of(tx), JANUARY_2024);

            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Doit propager l'exception si le solde est null")
        void shouldThrowWhenBalanceIsNull() {
            when(account.getBalance()).thenReturn(null);

            assertThatThrownBy(() -> calculator.calculate(account, Collections.emptyList(), JANUARY_2024))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("Doit fonctionner pour un mois sans aucun jour ouvré spécial (mois de 28 jours)")
        void shouldHandle28DayMonth() {
            YearMonth feb2023 = YearMonth.of(2023, 2); // 28 jours
            stubCurrentBalance(new BigDecimal("36500"));
            stubInterestCalculationWithRate(new BigDecimal("100"));

            BigDecimal result = calculator.calculate(account, Collections.emptyList(), feb2023);

            // 36500 * (100% / 365) * 28 = 100 * 27 = 2700
            assertThat(result).isEqualByComparingTo(new BigDecimal("2699.9999730000"));
        }

    }
}
