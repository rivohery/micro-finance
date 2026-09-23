package com.alibou.finance.history.infrastructure.adapter.out;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.history.domain.agregate.InterestRateTrace;
import com.alibou.finance.history.infrastructure.adapter.out.service.InterestRateTraceFactoryAdapter;
import com.alibou.finance.shared.domain.IllegalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class InterestRateTraceFactoryTest {
    @Mock
    private Account account;
    @InjectMocks
    private InterestRateTraceFactoryAdapter interestRateTraceFactory;

    // =====================================================================
    // ✅ CAS NOMINAL
    // =====================================================================
    @Nested
    @DisplayName("Cas nominal : tous les paramètres valides et positifs")
    class NominalCase {

        @Test
        @DisplayName("prepareToDataBase - construit une trace complète avec les bons champs")
        void shouldBuildTraceWithAllFieldsWhenParametersAreValid() {
            // ----- Given -----
            BigDecimal mgaExchangeRate = new BigDecimal("4500");
            BigDecimal monthlyInterestRate = new BigDecimal("1000.00");

            BigDecimal expectedMgaAmount = monthlyInterestRate.multiply(mgaExchangeRate);
            // 1000.00 * 4500 = 4 500 000.00

            LocalDateTime before = LocalDateTime.now();

            // ----- When -----
            InterestRateTrace trace = interestRateTraceFactory.prepare(account, mgaExchangeRate, monthlyInterestRate);

            LocalDateTime after = LocalDateTime.now();

            // ----- Then -----
            assertThat(trace).as("La trace ne doit pas être nulle").isNotNull();

            assertThat(trace.getInterestRateTraceId())
                    .as("Un identifiant doit être généré")
                    .isNotNull();

            assertThat(trace.getYear())
                    .as("L'année doit correspondre à l'année courante")
                    .isEqualTo(String.valueOf(before.getYear()))
                    .isEqualTo(String.valueOf(after.getYear()));

            assertThat(trace.getMonth())
                    .as("Le mois doit correspondre au mois courant")
                    .isEqualTo(before.getMonth().name())
                    .isEqualTo(after.getMonth().name());

            assertThat(trace.getMgaAmount())
                    .as("Le value object MgaAmount doit être présent")
                    .isNotNull();
            assertThat(trace.getMgaAmount().value())
                    .as("mgaAmount = monthlyInterestRate * mgaExchangeRate")
                    .isEqualByComparingTo(expectedMgaAmount);

            assertThat(trace.getAmount())
                    .as("Le value object Amount doit être présent")
                    .isNotNull();
            assertThat(trace.getAmount().value())
                    .as("amount = monthlyInterestRate")
                    .isEqualByComparingTo(monthlyInterestRate);

            assertThat(trace.getAccount())
                    .as("L'account doit être celui passé en paramètre")
                    .isSameAs(account);
        }

        @Test
        @DisplayName("prepareToDataBase - accepte un très petit taux positif (0.000000001)")
        void shouldAcceptTinyPositiveRate() {
            BigDecimal mgaExchangeRate = new BigDecimal("4500");
            BigDecimal monthlyInterestRate = new BigDecimal("0.000000001");

            InterestRateTrace trace = interestRateTraceFactory.prepare(
                    account, mgaExchangeRate, monthlyInterestRate);

            assertThat(trace).isNotNull();
            assertThat(trace.getAmount().value()).isEqualByComparingTo(monthlyInterestRate);
            assertThat(trace.getMgaAmount().value())
                    .isEqualByComparingTo(monthlyInterestRate.multiply(mgaExchangeRate));
        }
    }

    // =====================================================================
    // ❌ CAS D'ERREUR : un paramètre null => NPE (requireNonNull)
    // =====================================================================
    @Nested
    @DisplayName("Cas d'erreur : un paramètre null => NullPointerException (requireNonNull)")
    class NullParameters {

        @Test
        @DisplayName("prepareToDataBase - account null => NPE 'account must not be null'")
        void shouldThrowNpeWhenAccountIsNull() {
            BigDecimal mgaExchangeRate = BigDecimal.ONE;
            BigDecimal monthlyInterestRate = new BigDecimal("1000.00");

            assertThatNullPointerException()
                    .as("Un account null doit être rejeté")
                    .isThrownBy(() -> interestRateTraceFactory.prepare(
                            null, mgaExchangeRate, monthlyInterestRate))
                    .withMessage("account must not be null");
        }

        @Test
        @DisplayName("prepareToDataBase - mgaExchangeRate null => NPE 'mgaExchangeRate must not be null'")
        void shouldThrowNpeWhenMgaExchangeRateIsNull() {
            BigDecimal monthlyInterestRate = new BigDecimal("1000.00");

            assertThatNullPointerException()
                    .as("Un mgaExchangeRate null doit être rejeté")
                    .isThrownBy(() -> interestRateTraceFactory.prepare(
                            account, null, monthlyInterestRate))
                    .withMessage("mgaExchangeRate must not be null");
        }

        @Test
        @DisplayName("prepareToDataBase - monthlyInterestRate null => NPE 'monthlyInterestRate must not be null'")
        void shouldThrowNpeWhenMonthlyInterestRateIsNull() {
            BigDecimal mgaExchangeRate = BigDecimal.ONE;

            assertThatNullPointerException()
                    .as("Un monthlyInterestRate null doit être rejeté")
                    .isThrownBy(() -> interestRateTraceFactory.prepare(
                            account, mgaExchangeRate, null))
                    .withMessage("monthlyInterestRate must not be null");
        }
    }

    // =====================================================================
    // ❌ CAS D'ERREUR : valeurs non positives (rejetées par les Value Objects)
    // =====================================================================
    @Nested
    @DisplayName("Cas d'erreur : valeurs zéro ou négatives => retourne null")
    class NonPositiveValues {

        @Test
        @DisplayName("prepareToDataBase - monthlyInterestRate = 0 => le VO Amount rejette (non positif)")
        void shouldRejectZeroMonthlyInterestRate() {
            BigDecimal mgaExchangeRate = new BigDecimal("4500");
            BigDecimal monthlyInterestRate = BigDecimal.ZERO;

            InterestRateTrace result = interestRateTraceFactory.prepare(account, mgaExchangeRate, monthlyInterestRate);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("prepareToDataBase - mgaExchangeRate négatif => mgaAmount négatif => le VO MgaAmount rejette")
        void shouldRejectNegativeMgaExchangeRate() {
            BigDecimal mgaExchangeRate = new BigDecimal("-1.200");
            BigDecimal monthlyInterestRate = new BigDecimal("1000.00");

            assertThatThrownBy(() -> interestRateTraceFactory.prepare(
                    account, mgaExchangeRate, monthlyInterestRate))
                    .as("Un mgaExchangeRate négatif donne un mgaAmount négatif, rejeté par le VO MgaAmount")
                    .isInstanceOfAny(IllegalArgumentException.class, NullPointerException.class)
                    .hasMessage("MgaAmount doit être positive");
        }
    }

}
