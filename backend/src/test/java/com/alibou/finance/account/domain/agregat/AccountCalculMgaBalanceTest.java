package com.alibou.finance.account.domain.agregat;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.vo.account.AccountStatus;
import com.alibou.finance.account.domain.vo.account.Balance;
import com.alibou.finance.account.domain.vo.account.MgaBalance;
import com.alibou.finance.account.domain.vo.transaction.FinalAmount;
import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

public class AccountCalculMgaBalanceTest {

    private Account account;

    @BeforeEach
    void setUp(){
        account = Account.builder()
                .balance(Balance.init())
                .mgaBalance(new MgaBalance(BigDecimal.ZERO))
                .accountStatus(new AccountStatus(AccountStatusEnum.ACTIVE))
                .build();
    }

    @Test
    @DisplayName("calculMgaBalance avec un solde nul et un taux quelconque donne 0.00")
    void shouldReturnZeroMgaBalanceWhenBalanceIsZero() {
        // Given
        BigDecimal exchangeRate = new BigDecimal("4500.00");

        // When
        account.calculMgaBalance(exchangeRate);

        // Then
        assertThat(account.getMgaBalance().value())
                .isEqualByComparingTo(new BigDecimal("0.00"));
    }

    @Test
    @DisplayName("calculMgaBalance avec un solde positif et un taux entier")
    void shouldComputeMgaBalanceWithIntegerRate() {
        // Given
        account.makeDeposit(new FinalAmount(new BigDecimal("100.00")));
        BigDecimal exchangeRate = new BigDecimal("5");

        // When
        account.calculMgaBalance(exchangeRate);

        // Then
        assertThat(account.getMgaBalance().value())
                .isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("calculMgaBalance avec un taux décimal applique l'arrondi HALF_UP à 2 décimales")
    void shouldRoundHalfUpToTwoDecimals() {
        // Given
        account.makeDeposit(new FinalAmount(new BigDecimal("10.00")));
        BigDecimal exchangeRate = new BigDecimal("4500.5555"); // 10 * 4500.5555 = 45005.555

        // When
        account.calculMgaBalance(exchangeRate);

        // Then
        assertThat(account.getMgaBalance().value())
                .isEqualByComparingTo(new BigDecimal("45005.56"))
                .hasScaleOf(2);
    }

    @Test
    @DisplayName("calculMgaBalance arrondit correctement un demi (HALF_UP)")
    void shouldRoundHalfUpWhenThirdDecimalIsFive() {
        // Given
        account.makeDeposit(new FinalAmount(new BigDecimal("1.005"))); // solde = 1.005
        BigDecimal exchangeRate = new BigDecimal("1000");             // 1.005 * 1000 = 1005.000 -> 1005.00

        // When
        account.calculMgaBalance(exchangeRate);

        // Then
        assertThat(account.getMgaBalance().value())
                .isEqualByComparingTo(new BigDecimal("1005.00"));
    }

    @Test
    @DisplayName("calculMgaBalance avec un solde négatif (découvert) conserve le signe")
    void shouldHandleNegativeBalance() {

        // On contourne overdraftLimit null via le builder direct pour ce test
        Account accountWithNegative = Account.builder()
                .balance(Balance.init().subtract(new BigDecimal("30.00")))
                .mgaBalance(new MgaBalance(BigDecimal.ZERO))
                .build();
        BigDecimal exchangeRate = new BigDecimal("4500");

        // When
        accountWithNegative.calculMgaBalance(exchangeRate);

        // Then
        assertThat(accountWithNegative.getMgaBalance().value())
                .isEqualByComparingTo(new BigDecimal("-135000.00"));
    }

    @Test
    @DisplayName("calculMgaBalance écrase l'ancienne valeur de mgaBalance")
    void shouldOverwritePreviousMgaBalance() {
        // Given
        account.makeDeposit(new FinalAmount(new BigDecimal("100.00")));
        account.calculMgaBalance(new BigDecimal("10")); // 1000.00

        // When : nouveau calcul avec un autre taux
        account.calculMgaBalance(new BigDecimal("20"));

        // Then
        assertThat(account.getMgaBalance().value())
                .isEqualByComparingTo(new BigDecimal("2000.00"));
    }

}
