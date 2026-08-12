package com.alibou.finance.account.domain.agregate;

import com.alibou.finance.account.domain.exception.InactiveAccountException;
import com.alibou.finance.account.domain.exception.InsufficientBalanceException;
import com.alibou.finance.account.domain.vo.*;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.log.domain.vo.transaction.FinalAmount;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.shared.domain.IllegalOperationException;
import com.alibou.finance.shared.domain.OperationNotPermittedException;
import com.alibou.finance.account.domain.vo.AccountId;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Builder
@Getter
public class Account {
    private AccountId accountId;
    private AccountNumber accountNumber;
    private Balance balance;
    private MgaBalance mgaBalance;
    private AccountStatus accountStatus;
    private Currency currency;
    private AccountType accountType;
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;
    private CustomerId customerId;
    private OverdraftLimit overdraftLimit;//Découvert en monnaie du compte calculé à partir du solde minimale (en MGA) définie par type du compte


    public void initializeNewAccount(AccountNumber accountNumber, AccountType accountType, Currency currency,OverdraftLimit overdraftLimit){
        this.accountId = AccountId.generate();
        this.accountType = accountType;
        this.currency = currency;
        this.accountNumber = accountNumber;
        this.overdraftLimit = overdraftLimit;
        this.accountStatus = AccountStatus.pending();
        this.balance = Balance.init();
        this.mgaBalance = new MgaBalance(BigDecimal.ZERO);
    }

    public void calculMgaBalance(BigDecimal exchangeRateToMga){
        //On calcule mgaBalance à partir de la valeur du solde (balance) du compte à chaque nouvelle opération : deposit|withdraw
        BigDecimal mgaBalanceValue = this.balance.value().multiply(exchangeRateToMga).setScale(2, RoundingMode.HALF_UP);
        this.mgaBalance = new MgaBalance(mgaBalanceValue);
    }


    public void activeAccount(){
        if(this.accountStatus.value() == AccountStatusEnum.ACTIVE || this.accountStatus.value() == AccountStatusEnum.CLOSED){
            throw new IllegalOperationException("Soit le compte est déjà activé ou clôturé");
        }
        this.accountStatus = AccountStatus.active();
    }

    public void suspendAccount(){
        if(this.accountStatus.value() != AccountStatusEnum.ACTIVE){
            throw new IllegalOperationException("Seul le compte actif qu'on peut suspendre");
        }
        this.accountStatus = AccountStatus.suspend();
    }

    public void closeAccount(){
        if(this.balance.value().compareTo(BigDecimal.ZERO) > 0){
            throw new IllegalOperationException("Clôture interrompu car le solde n'est pas nulle");
        }
        this.accountStatus = AccountStatus.close();
    }

    public void updateBalanceOfDeposit(FinalAmount finalAmount){
        if(this.accountStatus.value() != AccountStatusEnum.ACTIVE){
            throw new InactiveAccountException("Ce compte n'est pas activé");
        }
        this.balance = this.balance.add(finalAmount.value());
    }

    public void updateBalanceOfWithdraw(FinalAmount finalAmount){
        if(this.accountStatus.value() != AccountStatusEnum.ACTIVE){
            throw new OperationNotPermittedException("Ce compte n'est pas activé");
        }
        BigDecimal potentialBalance = this.balance.subtract(finalAmount.value()).value();
        if(potentialBalance.compareTo(this.overdraftLimit.value()) <= 0){
            throw new InsufficientBalanceException("Solde insuffisant (limite de découvert dépassée).");
        }
        this.balance = this.balance.subtract(finalAmount.value());
    }

    public BigDecimal calculateInterestRateForSpecificDays(BigDecimal potentialSold, long nbrDays){
        String nbrDaysForYear = "365";
        BigDecimal dailyInterestRateOfAccount = this.accountType.getAnnualInterestRate().value().divide(new BigDecimal(nbrDaysForYear), 10, RoundingMode.HALF_UP);//précision de 10 chiffres après virgule
        BigDecimal dailyInterestRateFromSold = potentialSold.multiply(dailyInterestRateOfAccount);
        return dailyInterestRateFromSold.multiply(new BigDecimal("" + nbrDays));
    }

    public void addMonthlyInterestRate(BigDecimal monthlyInterestRate){
        this.balance = this.balance.add(monthlyInterestRate);
    }

    /*For Test only*/
    public void updateStatus(AccountStatus status){
        this.accountStatus = status;
    }

    public void updateBalance(Balance balance){
        this.balance = balance;
    }


}
