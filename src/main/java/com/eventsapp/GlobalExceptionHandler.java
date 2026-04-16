package com.eventsapp;

import com.eventsapp.registration.AlreadyRegisteredException;
import com.eventsapp.registration.EventFullException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleAlreadyRegistered(AlreadyRegisteredException ex) {
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler(EventFullException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEventFull(EventFullException ex) {
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(EntityNotFoundException ex) {
        return Map.of("message", ex.getMessage());
    }
}
