package com.eventsapp.registration.controller;

import com.eventsapp.registration.dto.RegisterRequest;
import com.eventsapp.registration.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events/{eventId}/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@PathVariable Long eventId, @RequestBody RegisterRequest req) {
        registrationService.register(eventId, req);
    }
}
