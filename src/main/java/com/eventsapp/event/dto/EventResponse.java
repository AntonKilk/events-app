package com.eventsapp.event.dto;

import java.time.OffsetDateTime;

public record EventResponse(
        Long id,
        String name,
        OffsetDateTime startsAt,
        int maxParticipants,
        long registrationsCount
) {}
