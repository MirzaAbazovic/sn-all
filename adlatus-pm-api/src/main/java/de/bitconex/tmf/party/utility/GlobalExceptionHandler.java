package de.bitconex.tmf.party.utility;

import de.bitconex.tmf.party.models.Error;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> globalExceptionHandler(Exception ex, WebRequest request) {
        System.out.println("GlobalExceptionHandler.globalExceptionHandler");
        return ResponseEntity.status(400).body(new Error().code("400").message(ex.getMessage()));
    }
}