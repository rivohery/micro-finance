package com.alibou.finance.customer.domain.vo;

import com.alibou.finance.shared.domain.Assert;
import com.alibou.finance.shared.domain.IllegalArgumentException;

import java.time.LocalDate;
import java.time.Period;

public record DateOfBirth(LocalDate value) {

    private static final int AGE_MAJEUR = 18;
    public DateOfBirth{
        Assert.notNull("Date de naissance", value);

        LocalDate now = LocalDate.now();
        if(value.isAfter(now)){
            throw new IllegalArgumentException("La date de naissance ne peut pas être dans le futur.");
        }

        int age = Period.between(value, now).getYears();
        if(age < AGE_MAJEUR){
            throw new IllegalArgumentException("Nos services sont réservés uniquement pour le majeur ");
        }
    }



    public static DateOfBirth from(LocalDate date){
        return new DateOfBirth(date);
    }
}
