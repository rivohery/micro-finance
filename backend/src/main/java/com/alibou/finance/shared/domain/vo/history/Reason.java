package com.alibou.finance.shared.domain.vo.history;

public record Reason(String value) {
    public Reason{
        if(value == null || value.isBlank()){
            value = "Reason non spécifié par l'employé";
        }
    }
}
