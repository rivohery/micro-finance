package com.alibou.finance.customer.infrastructure.handler;

import com.alibou.finance.customer.domain.exception.CustomerNotFoundException;
import com.alibou.finance.shared.infrastructure.error.HttpErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomerExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<HttpErrorResponse>handleException(CustomerNotFoundException ex){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        HttpErrorResponse.of(ex.getMessage(), HttpStatus.NOT_FOUND.value())
                );
    }

}
