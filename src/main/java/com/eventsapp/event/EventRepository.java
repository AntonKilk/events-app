package com.eventsapp.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = """
        SELECT e.id,
               e.name,
               e.starts_at,
               e.max_participants,
               (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.id) AS registrations_count
        FROM events e
        ORDER BY e.starts_at ASC
        """, nativeQuery = true)
    List<Object[]> findAllForListing();

    @Query(value = "SELECT * FROM events WHERE id = :id FOR UPDATE", nativeQuery = true)
    Optional<Event> findByIdForUpdate(@Param("id") Long id);
}
