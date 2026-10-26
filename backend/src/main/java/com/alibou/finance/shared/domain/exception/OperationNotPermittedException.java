package com.alibou.finance.shared.domain.exception;

public class OperationNotPermittedException extends RuntimeException{

    public OperationNotPermittedException() {
    }

    public OperationNotPermittedException(String message) {
        super(message);
    }
}
