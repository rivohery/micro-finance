package com.alibou.finance.history.domain.agregate;

import com.alibou.finance.shared.application.command.InterestTraceRateCommand;
import com.alibou.finance.history.domain.vo.interestRateTrace.MgaExchangeRate;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
import com.alibou.finance.history.domain.vo.interestRateTrace.InterestRateTraceId;
import com.alibou.finance.history.domain.vo.interestRateTrace.Amount;
import com.alibou.finance.history.domain.vo.interestRateTrace.MgaAmount;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;


@Getter
@Builder
public class InterestRateTrace {
    private InterestRateTraceId interestRateTraceId;
    private AccountNumber accountNumber;
    private InterestRate interestRate;//Taux d'intérêt appliqué
    private CurrencyCode currencyCode;
    private Amount amount;// montant en un mois en code monnaie du compte
    private MgaExchangeRate mgaExchangeRate;
    private MgaAmount mgaAmount;
    private String month;
    private String year;

    public static InterestRateTrace initialize(){
        LocalDate now = LocalDate.now();
        return InterestRateTrace.builder()
                .interestRateTraceId(InterestRateTraceId.generate())
                .year("" + now.getYear())
                .month(now.getMonth().name())
                .build();
    }


    public void buildFromCommand(InterestTraceRateCommand cmd){
        this.amount = new Amount(cmd.amount());
        this.currencyCode = cmd.currencyCode();
        this.interestRate = cmd.interestRate();
        this.mgaExchangeRate = new MgaExchangeRate(cmd.mgaExchangeRate());
        this.mgaAmount = new MgaAmount(cmd.mgaAmount());
        this.accountNumber = cmd.accountNumber();
    }

}



