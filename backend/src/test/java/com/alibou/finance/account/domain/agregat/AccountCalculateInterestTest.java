package com.alibou.finance.account.domain.agregat;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;
import com.alibou.finance.shared.domain.exception.IllegalArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Account - calculateInterestAmount")
class AccountCalculateInterestTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder().build();
    }

    // ------------------------------------------------------------------
    // Cas nominal
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Cas nominal")
    class NominalCase {

        @Test
        @DisplayName("Doit calculer les intérêts pour 30 jours à 12% sur 1 000 000")
        void should_compute_interest_for_30_days_at_12_percent() {
            // Given
            InterestRate rate = new InterestRate(new BigDecimal("12"));
            BigDecimal sold = new BigDecimal("1000000");
            account = Account.builder().annualInterestRate(rate).build();
            long days = 30;

            // When
            BigDecimal interest = account.calculateInterestAmount(sold, days);

            // Then
            // 1 000 000 × (12/100) × (30/365) = 9863.013698630137
            assertThat(interest.setScale(2, RoundingMode.HALF_UP))
                    .isEqualByComparingTo("9863.01");
        }

        @Test
        @DisplayName("Doit calculer les intérêts pour 365 jours = 1 an complet")
        void should_compute_interest_for_full_year() {
            // Given : taux 10%, solde 100 000, 365 jours → 10 000 exactement
            InterestRate rate = new InterestRate(new BigDecimal("10"));
            account = Account.builder().annualInterestRate(rate).build();
            BigDecimal sold = new BigDecimal("100000");

            // When
            BigDecimal interest = account.calculateInterestAmount(sold, 365);

            // Then
            assertThat(interest.setScale(2, RoundingMode.HALF_UP))
                    .isEqualByComparingTo("10000.00");
        }

        @Test
        @DisplayName("Doit calculer les intérêts pour 1 jour")
        void should_compute_interest_for_one_day() {
            InterestRate rate = new InterestRate(new BigDecimal("10"));
            account = Account.builder().annualInterestRate(rate).build();
            BigDecimal sold = new BigDecimal("100000");

            BigDecimal interest = account.calculateInterestAmount(sold, 1);

            // 100 000 × 0.10 / 365 = 27.397260...
            assertThat(interest.setScale(2, RoundingMode.HALF_UP))
                    .isEqualByComparingTo("27.40");
        }
    }

    // ------------------------------------------------------------------
    // Cas limites
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Cas limites")
    class EdgeCases {

        @Test
        @DisplayName("Doit retourner 0 si le solde est nul")
        void should_return_zero_when_balance_is_zero() {
            BigDecimal interest = account.calculateInterestAmount(
                    BigDecimal.ZERO,
                    30
            );

            assertThat(interest).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("Doit retourner 0 si le nombre de jours est 0")
        void should_return_zero_when_days_is_zero() {
            BigDecimal interest = account.calculateInterestAmount(
                    new BigDecimal("1000000"),
                    0
            );

            assertThat(interest).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("Doit retourner 0 si le taux est 0%")
        void should_return_zero_when_rate_is_zero() {
            account = Account.builder().annualInterestRate(new InterestRate(BigDecimal.ZERO)).build();
            BigDecimal interest = account.calculateInterestAmount(
                    new BigDecimal("1000000"),
                    30
            );

            assertThat(interest).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("Un solde négatif doit produire des intérêts négatifs")
        void should_produce_negative_interest_for_negative_balance() {
            // Cas d'un compte en découvert : les intérêts débiteurs sont négatifs
            account = Account.builder().annualInterestRate(new InterestRate(new BigDecimal("12"))).build();
            BigDecimal interest = account.calculateInterestAmount(
                    new BigDecimal("-50000"),
                    30
            );

            assertThat(interest).isLessThan(BigDecimal.ZERO);
            assertThat(interest.setScale(2, RoundingMode.HALF_UP))
                    .isEqualByComparingTo("-493.15");
        }
    }

    // ------------------------------------------------------------------
    // Robustesse / Précision
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Robustesse et précision")
    class RobustnessAndPrecision {

        @Test
        @DisplayName("Doit gérer un très grand nombre de jours sans overflow")
        void should_handle_large_number_of_days() {
            account = Account.builder().annualInterestRate(new InterestRate(new BigDecimal("10"))).build();
            BigDecimal interest = account.calculateInterestAmount(
                    new BigDecimal("1000000"),
                    3650   // 10 ans
            );

            // 1 000 000 × 0.10 × 10 = 1 000 000
            assertThat(interest).isEqualByComparingTo("999999.99");
        }

        @ParameterizedTest(name = "taux={0}%, jours={2} → intérêts attendus={3}")
        @CsvSource({
                "12,   1000000, 30,   9863.01",
                "10,   100000,  365,  10000.00",
                "5,    200000,  180,  4931.51",
                "0,    500000,  90,   0.00",
                "20,   10000,   7,    38.36",
        })
        @DisplayName("Vérifie plusieurs combinaisons taux/solde/jours")
        void should_compute_various_combinations(
                String rateStr, String soldStr, long days, String expectedStr) {
            account = Account.builder().annualInterestRate(new InterestRate(new BigDecimal(rateStr))).build();
            BigDecimal interest = account.calculateInterestAmount(
                    new BigDecimal(soldStr),
                    days
            );

            assertThat(interest.setScale(2, RoundingMode.HALF_UP))
                    .isEqualByComparingTo(expectedStr);
        }
    }

    // ------------------------------------------------------------------
    // Validation des entrées
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Validation des entrées")
    class InputValidation {

        @Test
        @DisplayName("Doit lever une exception si le taux annuel est null")
        void should_throw_when_rate_is_null() {
            assertThatThrownBy(() ->
                    account.calculateInterestAmount(new BigDecimal("1000"), 30))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Doit lever une exception si le solde est null")
        void should_throw_when_sold_is_null() {
            account = Account.builder().annualInterestRate(new InterestRate(new BigDecimal("12"))).build();
            assertThatThrownBy(() ->
                    account.calculateInterestAmount(null, 30))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Le solde ne peut pas être null");
        }

        @Test
        @DisplayName("Doit lever une exception si le nombre de jours est négatif")
        void should_throw_when_days_is_negative() {
            account = Account.builder().annualInterestRate(new InterestRate(new BigDecimal("12"))).build();
            // ⚠️ À implémenter si tu veux une validation métier explicite
            assertThatThrownBy(() ->
                    account.calculateInterestAmount(
                            new BigDecimal("1000"),
                            -5))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
