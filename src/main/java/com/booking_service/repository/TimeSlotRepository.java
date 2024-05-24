package com.booking_service.repository;

import com.booking_service.model.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    Optional<TimeSlot> findByName(String name);
}
