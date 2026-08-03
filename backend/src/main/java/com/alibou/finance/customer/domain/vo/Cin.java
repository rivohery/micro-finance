package com.alibou.finance.customer.domain.vo;

import com.alibou.finance.shared.domain.Assert;
import com.alibou.finance.shared.domain.IllegalArgumentException;

public record Cin(String value) {
    private static final String CIN_REGEX = "^[0-9]{12}$";
    public Cin{
        Assert.field("CIN", value).notEmpty();
        var cleanCIN = value.replaceAll("\\s+", ""); // Supprime tous les espaces
        if(!cleanCIN.matches(CIN_REGEX)){
            throw new IllegalArgumentException("Numéro CIN invalide (doit contenir 12 chiffres)");
        }
        value = cleanCIN;
    }
}
