package com.eventsapp.registration;

public class EventFullException extends RuntimeException {
    public EventFullException(Long eventId) {
        super("Event " + eventId + " has reached maximum capacity");
    }
}
