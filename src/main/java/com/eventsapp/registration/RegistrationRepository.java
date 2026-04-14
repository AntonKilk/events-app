package com.eventsapp.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    long countByEventId(Long eventId);

    @Transactional
    @Modifying
    @Query(value = """
        INSERT INTO registrations (event_id, first_name, last_name, id_number, created_at)
        VALUES (:eventId, :firstName, :lastName, :idNumber, current_timestamp)
        """, nativeQuery = true)
    void insert(@Param("eventId") Long eventId,
                @Param("firstName") String firstName,
                @Param("lastName") String lastName,
                @Param("idNumber") String idNumber);
}
