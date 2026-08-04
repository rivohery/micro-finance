package com.alibou.finance.account.application.port.dto.input;

import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.log.domain.vo.transaction.OriginalAmount;
import com.alibou.finance.log.domain.vo.transaction.TransactionCurrencyCode;
import com.alibou.finance.shared.vo.domain.Description;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TransactionInput{
    private AccountNumber concernedAccountNumber;  //Numéros du compte concerné
    private AccountNumber targetAccountNumber;  //pour le transfert, nulle pour depots et retrait
    private OriginalAmount originalAmount;  //montant avec la monnaie utilisée pour la transaction
    private TransactionCurrencyCode transactionCurrencyCode;  //on peut faire la transaction avec un autre monnaie que celle du compte | inutile pour le transfert
    private Description description;
    private User user;  //utilisateur qui fait la transaction

    public void buildTransactionCurrencyCodeFrom(String currency){
        this.transactionCurrencyCode = new TransactionCurrencyCode(currency);
    }

    public void updateConcernedAccountNumber(AccountNumber accountNumber){
        this.concernedAccountNumber = accountNumber;
    }
}



