package com.alibou.finance.currency.infrastructure.handler;

import com.alibou.finance.currency.domain.exception.CurrencyNotFoundException;
import com.alibou.finance.shared.error.infrastructure.HttpErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CurrencyExceptionHandler {

    @ExceptionHandler(CurrencyNotFoundException.class)
    public ResponseEntity<HttpErrorResponse> handleException(CurrencyNotFoundException exp) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.NOT_FOUND.value())
                );
    }
}
