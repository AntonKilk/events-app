package com.eventsapp.registration.repository;

import com.eventsapp.event.model.Event;
import com.eventsapp.event.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"spring.liquibase.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
@Testcontainers
@Transactional
class RegistrationRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    EventRepository eventRepository;

    private Event savedEvent(String name) {
        return eventRepository.save(
                Event.builder().name(name).startsAt(OffsetDateTime.now(ZoneOffset.UTC).plusDays(1)).maxParticipants(10).build());
    }

    @Test
    void countByEventId() {
        var event = savedEvent("Event");
        assertEquals(0L, registrationRepository.countByEventId(event.getId()));

        registrationRepository.insert(event.getId(), "John", "Doe", "ID-001");
        registrationRepository.insert(event.getId(), "Jane", "Smith", "ID-002");

        assertEquals(2L, registrationRepository.countByEventId(event.getId()));
    }

    @Test
    void insert() {
        var event1 = savedEvent("Event One");
        var event2 = savedEvent("Event Two");

        registrationRepository.insert(event1.getId(), "John", "Doe", "ID-001");
        registrationRepository.insert(event2.getId(), "John", "Doe", "ID-001");

        assertEquals(1L, registrationRepository.countByEventId(event1.getId()));
        assertEquals(1L, registrationRepository.countByEventId(event2.getId()));
        assertThrows(DataIntegrityViolationException.class, () ->
                registrationRepository.insert(event1.getId(), "Jane", "Smith", "ID-001"));
    }
}
