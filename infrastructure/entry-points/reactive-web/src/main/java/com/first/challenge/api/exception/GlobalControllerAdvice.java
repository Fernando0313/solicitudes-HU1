package com.first.challenge.api.exception;

import com.first.challenge.model.application.exceptions.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBusinessException(BusinessException ex, ServerWebExchange exchange) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(OffsetDateTime.now());
        error.setPath(exchange.getRequest().getPath().toString());
        error.setStatus(400);
        error.setError("Bad Request");
        error.setRequestId(UUID.randomUUID().toString());
        error.setErrorCode(ex.getCode());   // puede ser null, no se pinta
        error.setMessage(ex.getMessage()); // puede ser null, no se pinta

        return Mono.just(ResponseEntity.status(400).body(error));
    }

    @ExceptionHandler(Exception.class)
    public  Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex, ServerWebExchange exchange) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(OffsetDateTime.now());
        error.setPath(exchange.getRequest().getPath().toString());
        error.setStatus(500);
        error.setError("Internal Server Error");
        error.setRequestId(UUID.randomUUID().toString());

        return Mono.just(ResponseEntity.status(500).body(error));
    }
}
