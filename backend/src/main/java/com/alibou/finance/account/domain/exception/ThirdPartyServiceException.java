package com.alibou.finance.account.domain.exception;

public class ThirdPartyServiceException extends RuntimeException{
    public ThirdPartyServiceException() {
    }

    public ThirdPartyServiceException(String message) {
        super(message);
    }
}
