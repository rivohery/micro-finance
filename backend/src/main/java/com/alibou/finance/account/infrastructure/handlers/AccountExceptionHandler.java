package com.alibou.finance.account.infrastructure.handlers;

import com.alibou.finance.account.domain.exception.*;
import com.alibou.finance.shared.infrastructure.error.HttpErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AccountExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<HttpErrorResponse> handleException(AccountNotFoundException exp) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.NOT_FOUND.value())
                );
    }


    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<HttpErrorResponse> handleException(InsufficientBalanceException exp) {
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.NOT_ACCEPTABLE.value())
                );
    }

    @ExceptionHandler(ThirdPartyServiceException.class)
    public ResponseEntity<HttpErrorResponse> handleException(ThirdPartyServiceException exp) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)//code 503
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value())
                );
    }


    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<HttpErrorResponse> handleException(InactiveAccountException exp) {
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.NOT_ACCEPTABLE.value())
                );
    }
}
