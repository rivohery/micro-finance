package com.alibou.finance.customer.domain.exception;

public class CustomerNotFoundException extends RuntimeException{
    public CustomerNotFoundException() {
    }

    public CustomerNotFoundException(String message) {
        super(message);
    }
}
