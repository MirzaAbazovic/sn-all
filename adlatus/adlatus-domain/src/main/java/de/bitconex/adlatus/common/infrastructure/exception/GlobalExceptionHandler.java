package de.bitconex.adlatus.common.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CreateOrderInvalidException.class)
    public ResponseEntity<Object> handleCreateOrderInvalidException(CreateOrderInvalidException e) {
        log.debug("createResourceOrder: invalid resource order. ");
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Invalid resource order.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Object> handleJsonProcessingException(JsonProcessingException e) {
        log.error("createResourceOrder: failed with JsonProcessingException {} \"{}\"", e.getClass().getSimpleName(), e.getMessage(), e);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Failed with JsonProcessingException.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException e) {
        log.error("createResourceOrder: failed with exception {} \"{}\"", e.getClass().getSimpleName(), e.getMessage(), e);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Failed with exception.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}