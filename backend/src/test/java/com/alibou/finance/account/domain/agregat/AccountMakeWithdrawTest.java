package com.alibou.finance.account.domain.agregat;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.exception.InsufficientBalanceException;
import com.alibou.finance.account.domain.vo.account.AccountStatus;
import com.alibou.finance.account.domain.vo.account.Balance;
import com.alibou.finance.account.domain.vo.account.MgaBalance;
import com.alibou.finance.account.domain.vo.account.OverdraftLimit;
import com.alibou.finance.account.domain.vo.transaction.FinalAmount;
import com.alibou.finance.shared.domain.exception.OperationNotPermittedException;
import com.alibou.finance.shared.domain.vo.account.AccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Account - makeWithdraw")
class AccountMakeWithdrawTest {

    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("1000.00");
    private static final BigDecimal OVERDRAFT_LIMIT = new BigDecimal("-100.00");

    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .accountId(AccountId.generate())
                .balance(new Balance(INITIAL_BALANCE))
                .mgaBalance(new MgaBalance(BigDecimal.ZERO))
                .overdraftLimit(new OverdraftLimit(OVERDRAFT_LIMIT))
                .accountStatus(AccountStatus.active())
                .build();
    }

    // ------------------------------------------------------------------
    // Cas nominal
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Cas nominal")
    class NominalCase {

        @Test
        @DisplayName("Doit retirer le montant du solde quand le compte est actif et le solde suffisant")
        void should_withdraw_amount_when_account_active_and_balance_sufficient() {
            // Given
            FinalAmount withdrawAmount = new FinalAmount(new BigDecimal("300.00"));

            // When
            account.makeWithdraw(withdrawAmount);

            // Then
            assertThat(account.getBalance().value())
                    .isEqualByComparingTo("700.00");
        }

        @Test
        @DisplayName("Doit permettre un retrait qui amène le solde exactement à la limite de découvert")
        void should_allow_withdraw_when_reaching_exactly_overdraft_limit() {
            // Given : solde 1000, découvert -100 → retrait max autorisé = 1100
            FinalAmount withdrawAmount = new FinalAmount(new BigDecimal("1100.00"));

            // When
            account.makeWithdraw(withdrawAmount);

            // Then
            assertThat(account.getBalance().value())
                    .isEqualByComparingTo("-100.00");
        }
    }

    // ------------------------------------------------------------------
    // Cas d'erreur : statut du compte
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Statut du compte")
    class AccountStatusCases {

        @Test
        @DisplayName("Doit lever OperationNotPermittedException si le compte est PENDING")
        void should_throw_when_account_is_pending() {
            account = Account.builder()
                    .balance(new Balance(INITIAL_BALANCE))
                    .overdraftLimit(new OverdraftLimit(OVERDRAFT_LIMIT))
                    .accountStatus(AccountStatus.pending())
                    .build();

            FinalAmount withdrawAmount = new FinalAmount(new BigDecimal("100.00"));

            assertThatThrownBy(() -> account.makeWithdraw(withdrawAmount))
                    .isInstanceOf(OperationNotPermittedException.class)
                    .hasMessageContaining("Seul le compte actif qu'on peut faire du retrait");
        }

        @Test
        @DisplayName("Doit lever OperationNotPermittedException si le compte est SUSPENDED")
        void should_throw_when_account_is_suspended() {
            account = Account.builder()
                    .balance(new Balance(INITIAL_BALANCE))
                    .overdraftLimit(new OverdraftLimit(OVERDRAFT_LIMIT))
                    .accountStatus(AccountStatus.suspend())
                    .build();

            FinalAmount withdrawAmount = new FinalAmount(new BigDecimal("100.00"));

            assertThatThrownBy(() -> account.makeWithdraw(withdrawAmount))
                    .isInstanceOf(OperationNotPermittedException.class);
        }

        @Test
        @DisplayName("Doit lever OperationNotPermittedException si le compte est CLOSED")
        void should_throw_when_account_is_closed() {
            account = Account.builder()
                    .balance(new Balance(BigDecimal.ZERO))
                    .overdraftLimit(new OverdraftLimit(OVERDRAFT_LIMIT))
                    .accountStatus(AccountStatus.close())
                    .build();

            FinalAmount withdrawAmount = new FinalAmount(new BigDecimal("100.00"));

            assertThatThrownBy(() -> account.makeWithdraw(withdrawAmount))
                    .isInstanceOf(OperationNotPermittedException.class);
        }
    }

    // ------------------------------------------------------------------
    // Cas d'erreur : solde insuffisant
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Solde insuffisant")
    class InsufficientBalanceCases {

        @Test
        @DisplayName("Doit lever InsufficientBalanceException si le retrait dépasse la limite de découvert")
        void should_throw_when_withdraw_exceeds_overdraft_limit() {
            // Given : solde 1000, découvert -100 → retrait de 1200 refusé
            FinalAmount withdrawAmount = new FinalAmount(new BigDecimal("1200.00"));

            assertThatThrownBy(() -> account.makeWithdraw(withdrawAmount))
                    .isInstanceOf(InsufficientBalanceException.class)
                    .hasMessageContaining("Solde insuffisant (limite de découvert dépassée).");
        }

        @Test
        @DisplayName("Le solde ne doit pas être modifié quand le retrait échoue")
        void should_not_change_balance_when_withdraw_fails() {
            FinalAmount withdrawAmount = new FinalAmount(new BigDecimal("5000.00"));

            assertThatThrownBy(() -> account.makeWithdraw(withdrawAmount))
                    .isInstanceOf(InsufficientBalanceException.class);

            assertThat(account.getBalance().value())
                    .isEqualByComparingTo(INITIAL_BALANCE);
        }

        @Test
        @DisplayName("Doit lever une exception si le solde est déjà à la limite de découvert")
        void should_throw_when_balance_already_at_overdraft_limit() {
            account = Account.builder()
                    .balance(new Balance(new BigDecimal("-100.00")))
                    .overdraftLimit(new OverdraftLimit(OVERDRAFT_LIMIT))
                    .accountStatus(AccountStatus.active())
                    .build();

            FinalAmount withdrawAmount = new FinalAmount(new BigDecimal("0.01"));

            assertThatThrownBy(() -> account.makeWithdraw(withdrawAmount))
                    .isInstanceOf(InsufficientBalanceException.class);
        }
    }
}