package com.eventsapp.event;

import com.eventsapp.event.dto.CreateEventRequest;
import com.eventsapp.event.dto.EventResponse;
import com.eventsapp.registration.RegistrationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public EventService(EventRepository eventRepository, RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    public List<EventResponse> listEvents() {
        return eventRepository.findAll(Sort.by("startsAt")).stream()
                .map(e -> new EventResponse(
                        e.getId(), e.getName(), e.getStartsAt(), e.getMaxParticipants(),
                        registrationRepository.countByEventId(e.getId())))
                .toList();
    }

    @Transactional
    public Event createEvent(CreateEventRequest req) {
        if (req.name() == null || req.name().isBlank()) {
            throw new IllegalArgumentException("Event name must not be blank");
        }
        if (req.startsAt() == null) {
            throw new IllegalArgumentException("Event start time is required");
        }
        if (req.maxParticipants() <= 0) {
            throw new IllegalArgumentException("Max participants must be greater than zero");
        }
        return eventRepository.save(Event.builder()
                .name(req.name())
                .startsAt(req.startsAt())
                .maxParticipants(req.maxParticipants())
                .build());
    }
}
