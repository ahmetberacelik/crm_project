package com.crm.user.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationErrors(WebExchangeBindException ex, ServerWebExchange exchange) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Validation Failed")
                .validationErrors(errors)
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().toString())
                .build();
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleRuntimeException(RuntimeException ex, ServerWebExchange exchange) {
        ErrorResponse.ErrorResponseBuilder responseBuilder = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().toString());

        HttpStatus status;

        String message = ex.getMessage();
        if (message != null && message.contains("Invalid credentials")) {
            status = HttpStatus.UNAUTHORIZED;
            responseBuilder.message("Invalid email or password.");
        } else if (message != null && message.contains("Email already in use")) {
            status = HttpStatus.BAD_REQUEST;
            responseBuilder.message(message);
        } else {
            // Log the full exception for debugging purposes on the server side
            // log.error("An unexpected error occurred", ex); 
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            responseBuilder.message("An unexpected internal server error occurred.");
        }

        ErrorResponse errorResponse = responseBuilder.statusCode(status.value()).build();
        return Mono.just(new ResponseEntity<>(errorResponse, status));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("Invalid email or password.") // Keep the message generic for security
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
} 