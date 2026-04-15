package com.eventsapp.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"spring.liquibase.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
@Testcontainers
@Transactional
class EventRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    EventRepository eventRepository;

    private Event event(String name, int daysFromNow, int maxParticipants) {
        return Event.builder().name(name).startsAt(OffsetDateTime.now(ZoneOffset.UTC).plusDays(daysFromNow)).maxParticipants(maxParticipants).build();
    }

    @Test
    void findByIdForUpdate() {
        var saved = eventRepository.save(event("Conference", 3, 20));

        var found = eventRepository.findByIdForUpdate(saved.getId());
        var missing = eventRepository.findByIdForUpdate(999L);

        assertTrue(found.isPresent());
        assertEquals("Conference", found.get().getName());
        assertEquals(20, found.get().getMaxParticipants());
        assertTrue(missing.isEmpty());
    }
}
