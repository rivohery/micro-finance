package com.alibou.finance.auth.infrastructure.error;

import com.alibou.finance.shared.error.infrastructure.HttpErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpErrorResponse> handleException(LockedException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.UNAUTHORIZED.value())
                );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpErrorResponse> handleException(DisabledException exp) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.FORBIDDEN.value())
                );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpErrorResponse> handleException(AccessDeniedException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        HttpErrorResponse.of(exp.getMessage() + ": some role is required to access this resource", HttpStatus.UNAUTHORIZED.value())
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpErrorResponse> handleException(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        HttpErrorResponse.of("Login or password Incorrect", HttpStatus.UNAUTHORIZED.value())
                );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<HttpErrorResponse> handleException(UsernameNotFoundException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.UNAUTHORIZED.value())
                );
    }
    @ExceptionHandler(ExpiredJwtException.class)
    private ResponseEntity<HttpErrorResponse>handleException(ExpiredJwtException exp){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.UNAUTHORIZED.value())
                );
    }
}
