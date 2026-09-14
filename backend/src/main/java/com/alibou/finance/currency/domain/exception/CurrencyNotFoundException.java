package com.alibou.finance.currency.domain.exception;

public class CurrencyNotFoundException extends RuntimeException{
    public CurrencyNotFoundException() {
    }

    public CurrencyNotFoundException(String message) {
        super(message);
    }
}
