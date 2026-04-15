package com.eventsapp.event;

import com.eventsapp.event.dto.CreateEventRequest;
import com.eventsapp.event.dto.EventResponse;
import com.eventsapp.event.model.Event;
import com.eventsapp.event.repository.EventRepository;
import com.eventsapp.event.service.EventService;
import com.eventsapp.registration.repository.RegistrationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    EventRepository eventRepository;

    @Mock
    RegistrationRepository registrationRepository;

    @InjectMocks
    EventService eventService;

    private static final OffsetDateTime FUTURE = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1);

    @Test
    void listEvents() {
        var e1 = Event.builder().name("First").startsAt(FUTURE).maxParticipants(10).build();
        var e2 = Event.builder().name("Second").startsAt(FUTURE.plusDays(1)).maxParticipants(20).build();
        when(eventRepository.findAll(Sort.by("startsAt"))).thenReturn(List.of(e1, e2));
        when(registrationRepository.countByEventId(any())).thenReturn(0L, 3L);

        List<EventResponse> result = eventService.listEvents();

        assertEquals(2, result.size());
        assertEquals("First", result.get(0).name());
        assertEquals(0L, result.get(0).registrationsCount());
        assertEquals("Second", result.get(1).name());
        assertEquals(3L, result.get(1).registrationsCount());
    }

    @Test
    void createEvent() {
        var req = new CreateEventRequest("Conference", FUTURE, 50);
        var saved = Event.builder().name("Conference").startsAt(FUTURE).maxParticipants(50).build();
        when(eventRepository.save(any())).thenReturn(saved);

        var result = eventService.createEvent(req);

        assertEquals("Conference", result.getName());
        assertEquals(50, result.getMaxParticipants());
        verify(eventRepository).save(any());
    }

    @Test
    void createEventValidation() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.createEvent(new CreateEventRequest("", FUTURE, 10)));
        assertThrows(IllegalArgumentException.class, () ->
                eventService.createEvent(new CreateEventRequest("Name", null, 10)));
        assertThrows(IllegalArgumentException.class, () ->
                eventService.createEvent(new CreateEventRequest("Name", FUTURE, 0)));
        verifyNoInteractions(eventRepository);
    }
}
