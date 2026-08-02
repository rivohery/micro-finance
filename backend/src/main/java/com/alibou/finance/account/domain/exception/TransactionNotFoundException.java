package com.alibou.finance.account.domain.exception;

public class TransactionNotFoundException extends RuntimeException{
    public TransactionNotFoundException() {
    }

    public TransactionNotFoundException(String message) {
        super(message);
    }
}
