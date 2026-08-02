package com.alibou.finance.currency.domain.agregate;

import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.currency.domain.vo.CurrencyId;
import com.alibou.finance.currency.domain.vo.CurrencyName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Currency {
    private CurrencyId currencyId;
    private CurrencyName name;
    private CurrencyCode code;
    private boolean enable;

    public Currency(CurrencyCode code){
        this.code = code;
    }

    public void generateCurrencyId(){
        this.currencyId = CurrencyId.generate();
    }

    public void active(){
        this.enable = true;
    }


}
