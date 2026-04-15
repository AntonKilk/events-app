package com.eventsapp.registration.controller;

import com.eventsapp.registration.dto.RegisterRequest;
import com.eventsapp.registration.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@PathVariable Long eventId, @RequestBody RegisterRequest req) {
        registrationService.register(eventId, req);
    }
}
