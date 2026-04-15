package com.eventsapp.event.dto;

import java.time.OffsetDateTime;

public record CreateEventRequest(String name, OffsetDateTime startsAt, int maxParticipants) {}
