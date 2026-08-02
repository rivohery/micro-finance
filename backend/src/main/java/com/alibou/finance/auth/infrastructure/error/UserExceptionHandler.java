package com.alibou.finance.auth.infrastructure.error;

import com.alibou.finance.auth.domain.exception.UserNotFoundException;
import com.alibou.finance.shared.error.infrastructure.HttpErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpErrorResponse> handleException(UserNotFoundException exp) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.NOT_FOUND.value())
                );
    }
}
