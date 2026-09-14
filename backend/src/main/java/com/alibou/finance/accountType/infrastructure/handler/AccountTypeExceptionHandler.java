package com.alibou.finance.accountType.infrastructure.handler;

import com.alibou.finance.accountType.domain.exception.AccountTypeNotFoundException;
import com.alibou.finance.shared.infrastructure.error.HttpErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AccountTypeExceptionHandler {

    @ExceptionHandler(AccountTypeNotFoundException.class)
    public ResponseEntity<HttpErrorResponse> handleException(AccountTypeNotFoundException exp) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.NOT_FOUND.value())
                );
    }
}
