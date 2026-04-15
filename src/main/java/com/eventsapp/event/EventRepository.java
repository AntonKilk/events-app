package com.eventsapp.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = "SELECT * FROM events WHERE id = :id FOR UPDATE", nativeQuery = true)
    Optional<Event> findByIdForUpdate(@Param("id") Long id);
}
