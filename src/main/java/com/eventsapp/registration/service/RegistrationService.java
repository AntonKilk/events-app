package com.eventsapp.registration.service;

import com.eventsapp.registration.EventFullException;
import com.eventsapp.registration.dto.RegisterRequest;
import com.eventsapp.registration.repository.RegistrationRepository;
import com.eventsapp.event.model.Event;
import com.eventsapp.event.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public RegistrationService(EventRepository eventRepository,
                               RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    @Transactional
    public void register(Long eventId, RegisterRequest req) {
        Event event = eventRepository.findByIdForUpdate(eventId)
            .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId));

        long count = registrationRepository.countByEventId(eventId);
        if (count >= event.getMaxParticipants()) {
            throw new EventFullException(eventId);
        }

        registrationRepository.insert(eventId, req.firstName(), req.lastName(), req.idNumber());
    }
}
