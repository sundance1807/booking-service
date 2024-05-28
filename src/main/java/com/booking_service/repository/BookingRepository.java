package com.booking_service.repository;

import com.booking_service.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT * FROM bookings WHERE room_id = :roomId AND start_time BETWEEN :startOfWeek AND :endOfWeek", nativeQuery = true)
    List<Booking> getWeekBookings(Long roomId, LocalDateTime startOfWeek, LocalDateTime endOfWeek);

    @Query(value = "SELECT * FROM bookings WHERE room_id = :roomId AND (start_time < :endTime AND end_time > :startTime)", nativeQuery = true)
    List<Booking> findOverlappingBookings(Long roomId, LocalDateTime startTime, LocalDateTime endTime);
}
