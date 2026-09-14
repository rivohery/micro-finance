package com.alibou.finance.shared.domain;

public class IllegalOperationException extends RuntimeException{
    public IllegalOperationException() {
    }

    public IllegalOperationException(String message) {
        super(message);
    }
}
