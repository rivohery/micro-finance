package com.alibou.finance.account.domain.agregate;

import com.alibou.finance.account.domain.exception.InsufficientBalanceException;
import com.alibou.finance.account.domain.vo.account.AccountStatus;
import com.alibou.finance.account.domain.vo.account.Balance;
import com.alibou.finance.account.domain.vo.account.MgaBalance;
import com.alibou.finance.account.domain.vo.account.OverdraftLimit;
import com.alibou.finance.accountType.AccountTypeInfo;
import com.alibou.finance.currency.CurrencyInfo;
import com.alibou.finance.account.domain.vo.transaction.FinalAmount;
import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.domain.exception.IllegalArgumentException;
import com.alibou.finance.shared.domain.exception.IllegalOperationException;
import com.alibou.finance.shared.domain.exception.OperationNotPermittedException;
import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeName;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
import com.alibou.finance.shared.domain.vo.currency.CurrencyId;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

@Builder
@Getter
public class Account {

    private static final String NB_DAYS_OF_YEAR = "365";
    private AccountId accountId;
    private AccountNumber accountNumber;
    private Balance balance;
    private MgaBalance mgaBalance;
    private AccountStatus accountStatus;
    private CurrencyId currencyId;
    private CurrencyCode currencyCode;
    private AccountTypeId accountTypeId;
    private AccountTypeName accountTypeName;
    private AccountTypeCode accountTypeCode;
    private InterestRate annualInterestRate;
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;
    private CustomerId customerId;
    private OverdraftLimit overdraftLimit;//Découvert en monnaie du compte calculé à partir du solde minimale (en MGA) définie par type du compte


    public static Account initializeNewAccount(
            AccountNumber accountNumber,
            AccountTypeInfo accountType,
            CurrencyInfo currency,
            OverdraftLimit overdraftLimit,
            CustomerId customerId
    ){
        return Account.builder()
                .accountId(AccountId.generate())
                .accountTypeId(accountType.accountTypeId())
                .accountTypeName(accountType.accountTypeName())
                .accountTypeCode(accountType.accountTypeCode())
                .annualInterestRate(accountType.annualInterestRate())
                .currencyId(currency.currencyId())
                .currencyCode(currency.currencyCode())
                .accountNumber(accountNumber)
                .overdraftLimit(overdraftLimit)
                .accountStatus(AccountStatus.pending())
                .balance(Balance.init())
                .mgaBalance(new MgaBalance(BigDecimal.ZERO))
                .customerId(customerId)
                .build();
    }

    public void calculMgaBalance(BigDecimal exchangeRateToMga){
        //On calcule mgaBalance à partir de la valeur du solde (balance) du compte à chaque nouvelle opération : deposit|withdraw
        if(Objects.isNull(exchangeRateToMga)){
            throw new OperationNotPermittedException("On ne peut pas calculer mgaBalance avec taux d'échange en MGA nulle");
        }
        BigDecimal mgaBalanceValue = this.balance.value().multiply(exchangeRateToMga).setScale(2, RoundingMode.HALF_UP);
        this.mgaBalance = new MgaBalance(mgaBalanceValue);
    }


    public void active(){
        if (this.accountStatus.value() != AccountStatusEnum.PENDING) {
            throw new IllegalOperationException(
                    "Seul un compte en attente peut être activé"
            );
        }
        this.accountStatus = AccountStatus.active();
    }

    public void reactive() {
        if (this.accountStatus.value() != AccountStatusEnum.SUSPENDED) {
            throw new IllegalOperationException(
                    "Seul un compte suspendu peut être réactivé"
            );
        }
        this.accountStatus = AccountStatus.active();
    }

    public void suspend() {
        if (this.accountStatus.value() != AccountStatusEnum.ACTIVE) {
            throw new IllegalOperationException(
                    "Seul un compte actif peut être suspendu"
            );
        }
        this.accountStatus = AccountStatus.suspend();
    }

    public void close() {
        if (this.accountStatus.value() == AccountStatusEnum.CLOSED) {
            throw new IllegalOperationException("Compte déjà clôturé");
        }
        if (this.balance.value().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalOperationException(
                    "Clôture impossible : le solde doit être nul"
            );
        }
        this.accountStatus = AccountStatus.close();
    }


    public void makeDeposit(FinalAmount finalAmount){
        if(this.accountStatus.value() == AccountStatusEnum.CLOSED){
            throw new IllegalOperationException("Ce compte est deja clôturé, aucune operation n'est plus valable");
        }
        this.balance = this.balance.add(finalAmount.value());
    }

    public void makeWithdraw(FinalAmount finalAmount){
        if(this.accountStatus.value() != AccountStatusEnum.ACTIVE){
            throw new OperationNotPermittedException("Seul le compte actif qu'on peut faire du retrait");
        }
        BigDecimal potentialBalance = this.balance.subtract(finalAmount.value()).value();
        if(potentialBalance.compareTo(this.overdraftLimit.value()) < 0){
            throw new InsufficientBalanceException("Solde insuffisant (limite de découvert dépassée).");
        }
        this.balance = this.balance.subtract(finalAmount.value());
    }

    public BigDecimal calculateInterestAmount(BigDecimal potentialSold, long nbrDays){
        if (potentialSold == null) {
            throw new IllegalArgumentException("Le solde ne peut pas être null");
        }
        if (nbrDays < 0) {
            throw new IllegalArgumentException("Le nombre de jours ne peut pas être négatif");
        }
        if (nbrDays == 0 || potentialSold.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        //annualInterestRate valeur en %tage
        BigDecimal dailyInterestRateOfAccount = this.annualInterestRate.value()
                .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP) //pour éviter le taux non terminable (3.33%)
                .divide(new BigDecimal(NB_DAYS_OF_YEAR), 10, RoundingMode.HALF_UP);//précision de 10 chiffres après virgule
        return potentialSold.multiply(dailyInterestRateOfAccount).multiply(BigDecimal.valueOf(nbrDays));
    }

    public void addMonthlyInterestRate(BigDecimal monthlyInterestRate){
        this.balance = this.balance.add(monthlyInterestRate);
    }


}
