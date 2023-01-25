package com.reactivespring.globalerrorhandler;

import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(MoviesInfoClientException.class)
    public ResponseEntity<String> handleRequestClientException(MoviesInfoClientException e) {
        log.error("Exception Caught in handleRequestClientException : {} ", e.getMessage(), e);
        return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRequestServerException(RuntimeException e) {
        log.error("Exception Caught in handleRequestServerException : {} ", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<String> handleServerDownException(WebClientRequestException e) {
        log.error("Exception Caught in handleServerDownException : {} ", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Problem encountered connecting to MovieInfoServer: " + e.getMessage());
    }

}
