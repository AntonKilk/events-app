package com.eventsapp.event.controller;

import com.eventsapp.event.dto.CreateEventRequest;
import com.eventsapp.event.dto.EventResponse;
import com.eventsapp.event.model.Event;
import com.eventsapp.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<EventResponse> list() {
        return eventService.listEvents();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse create(@RequestBody CreateEventRequest req) {
        Event event = eventService.createEvent(req);
        return new EventResponse(event.getId(), event.getName(), event.getStartsAt(), event.getMaxParticipants(), 0);
    }
}
