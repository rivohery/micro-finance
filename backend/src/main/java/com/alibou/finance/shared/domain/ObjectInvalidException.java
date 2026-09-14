package com.alibou.finance.shared.domain;

public class ObjectInvalidException extends RuntimeException{
    public ObjectInvalidException() {
    }

    public ObjectInvalidException(String message) {
        super(message);
    }
}
