package com.alibou.finance.log.domain.agregate;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.log.domain.vo.interestRateTrace.InterestRateTraceId;
import com.alibou.finance.log.domain.vo.interestRateTrace.Amount;
import com.alibou.finance.log.domain.vo.interestRateTrace.MgaAmount;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class InterestRateTrace {
    private InterestRateTraceId interestRateTraceId;
    private Account account;
    private Amount amount;
    private MgaAmount mgaAmount;
    private String month;
    private String year;

}


