package com.alibou.finance.accountType.domain.agregate;

import com.alibou.finance.accountType.application.command.AccountTypeCommand;
import com.alibou.finance.accountType.domain.vo.*;
import com.alibou.finance.shared.domain.vo.accountType.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountType {
    private AccountTypeId accountTypeId;
    private AccountTypeName name;
    private AccountTypeCode code;
    private AccountFee accountFee;
    private InterestRate annualInterestRate;
    private MinimumBalance minimumBalance;//solde minimum (découvert) pour certain type de compte (ex : compte épargne exige un solde bloqué 300MGA - compte courant 0MGA)

    //On peut ajouter autres attributs en fonction de besoin metier : "withdrawalLimit",...
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;


    public static AccountType createFrom(AccountTypeCommand command){
        return AccountType.builder()
                .accountTypeId(AccountTypeId.generate())
                .name(command.accountTypeName())
                .code(command.accountTypeCode())
                .annualInterestRate(command.annualInterestRate())
                .accountFee(command.accountFee())
                .minimumBalance(command.minimumBalance())
                .build();
    }

    public Set<String> updateFrom (AccountTypeCommand command){
        Set<String>updatedField = new HashSet<>();
        if(!Objects.equals(this.code, command.accountTypeCode())){
            this.code = command.accountTypeCode();
            updatedField.add("code");
        }
        if(!Objects.equals(this.name, command.accountTypeName())){
            this.name = command.accountTypeName();
            updatedField.add("name");
        }
        if(this.annualInterestRate.value().compareTo(command.annualInterestRate().value()) != 0){
            this.annualInterestRate = command.annualInterestRate();
            updatedField.add("annualInterestRate");
        }
        if(this.minimumBalance.value().compareTo(command.minimumBalance().value()) != 0){
            this.minimumBalance = command.minimumBalance();
            updatedField.add("minimumBalance");
        }
        if(this.accountFee.value().compareTo(command.accountFee().value()) != 0){
            this.accountFee = command.accountFee();
            updatedField.add("accountFee");
        }
        return updatedField;
    }

    public AccountType(AccountTypeCode code){
        this.code = code;
    }

    public AccountType(AccountTypeName name){
        this.name = name;
    }

}
