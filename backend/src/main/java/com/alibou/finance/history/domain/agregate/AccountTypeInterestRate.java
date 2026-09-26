package com.alibou.finance.history.domain.agregate;

import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;
import com.alibou.finance.history.application.port.command.AccountTypeInterestRateCommand;
import com.alibou.finance.history.domain.vo.accountTypeRate.AccountTypeRateId;
import com.alibou.finance.history.domain.vo.accountTypeRate.Annee;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class AccountTypeInterestRate {
    private AccountTypeRateId accountTypeRateId;

    private AccountTypeId accountTypeId;

    private AccountTypeCode accountTypeCode;

    private InterestRate annualInterestRate;

    private Annee annee;

    public static AccountTypeInterestRate prepare(AccountTypeInterestRateCommand command){
        String year = "" + LocalDate.now().getYear();
        return AccountTypeInterestRate
                .builder()
                .accountTypeRateId(AccountTypeRateId.generate())
                .accountTypeId(command.accountTypeId())
                .accountTypeCode(command.accountTypeCode())
                .annualInterestRate(command.annualInterestRate())
                .annee(new Annee(year))
                .build();
    }


}
