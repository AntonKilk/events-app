package com.eventsapp.registration.service;

import com.eventsapp.event.model.Event;
import com.eventsapp.event.repository.EventRepository;
import com.eventsapp.registration.EventFullException;
import com.eventsapp.registration.dto.RegisterRequest;
import com.eventsapp.registration.repository.RegistrationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    EventRepository eventRepository;

    @Mock
    RegistrationRepository registrationRepository;

    @InjectMocks
    RegistrationService registrationService;

    private static final RegisterRequest REQ = new RegisterRequest("John", "Doe", "ID-001");

    @Test
    void register() {
        when(eventRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(event(10)));
        when(registrationRepository.countByEventId(1L)).thenReturn(5L);

        registrationService.register(1L, REQ);

        verify(registrationRepository).insert(1L, "John", "Doe", "ID-001");
    }

    @Test
    void registerThrowsWhenEventNotFound() {
        when(eventRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                registrationService.register(99L, REQ));

        verifyNoInteractions(registrationRepository);
    }

    @Test
    void registerThrowsWhenEventFull() {
        when(eventRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(event(10)));
        when(registrationRepository.countByEventId(1L)).thenReturn(10L);

        assertThrows(EventFullException.class, () ->
                registrationService.register(1L, REQ));

        verify(registrationRepository, never()).insert(any(), any(), any(), any());
    }

    @Test
    void registerAllowsLastSpot() {
        when(eventRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(event(10)));
        when(registrationRepository.countByEventId(1L)).thenReturn(9L);

        registrationService.register(1L, REQ);

        verify(registrationRepository).insert(eq(1L), any(), any(), any());
    }

    private Event event(int maxParticipants) {
        return Event.builder()
                .name("Conference")
                .startsAt(OffsetDateTime.now(ZoneOffset.UTC).plusDays(1))
                .maxParticipants(maxParticipants)
                .build();
    }
}
