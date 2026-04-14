package com.eventsapp.event;

import com.eventsapp.registration.RegistrationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"spring.liquibase.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
@Testcontainers
@Transactional
class EventRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    EventRepository eventRepository;

    @Autowired
    RegistrationRepository registrationRepository;

    private Event event(String name, int daysFromNow, int maxParticipants) {
        return Event.builder().name(name).startsAt(OffsetDateTime.now().plusDays(daysFromNow)).maxParticipants(maxParticipants).build();
    }

    @Test
    void findAllForListing() {
        var later = eventRepository.save(event("Later", 2, 50));
        var earlier = eventRepository.save(event("Earlier", 1, 30));
        var noRegs = eventRepository.save(event("NoRegs", 3, 10));
        registrationRepository.insert(earlier.getId(), "John", "Doe", "ID-001");
        registrationRepository.insert(later.getId(), "Jane", "Smith", "ID-002");
        registrationRepository.insert(later.getId(), "Bob", "Jones", "ID-003");

        List<Object[]> rows = eventRepository.findAllForListing();

        assertEquals(3, rows.size());

        assertEquals(earlier.getId(), ((Number) rows.get(0)[0]).longValue());
        assertEquals(later.getId(), ((Number) rows.get(1)[0]).longValue());
        assertEquals(noRegs.getId(), ((Number) rows.get(2)[0]).longValue());

        assertEquals(1L, ((Number) rows.get(0)[4]).longValue());
        assertEquals(2L, ((Number) rows.get(1)[4]).longValue());
        assertEquals(0L, ((Number) rows.get(2)[4]).longValue());
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
