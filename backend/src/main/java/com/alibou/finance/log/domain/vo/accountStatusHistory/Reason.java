package com.alibou.finance.log.domain.vo.accountStatusHistory;

public record Reason(String value) {
    public Reason{
        if(value == null || value.isBlank()){
            value = "Reason non spécifié par l'employé";
        }
    }
}
