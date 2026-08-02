package com.alibou.finance.account.domain.agregate;

import com.alibou.finance.account.domain.vo.*;
import com.alibou.finance.log.domain.vo.InterestRate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountType {
    private AccountTypeId accountTypeId;
    private AccountTypeName name;//Épargne-courante-business
    private AccountTypeCode code; // 10=>courante;20=>épargne;30=>business
    private AccountFee accountFee;// Frais du compte pour le compte courant par exemple
    private InterestRate annualInterestRate;// Taux d'intérêt annuel en pourcentage
    private MinimumBalance minimumBalance;//solde minimum (découvert) pour certain type de compte (ex : compte épargne exige un solde bloqué 300MGA - compte courant 0MGA)

    //On peut ajouter autres attributs en fonction de besoin metier : "withdrawalLimit",...
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;

    public void buildAccountTypeIdFrom(UUID id){
        this.accountTypeId = AccountTypeId.from(id);
    }

    public AccountType(AccountTypeCode code){
        this.code = code;
    }

    public AccountType(AccountTypeName name){
        this.name = name;
    }

}
