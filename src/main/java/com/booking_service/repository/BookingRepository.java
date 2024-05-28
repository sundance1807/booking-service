package com.booking_service.repository;

import com.booking_service.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(nativeQuery = true,
    value = "select * from booking where date between :monday and :sunday and room_id = :roomId")
    List<Booking> getWeekBookings(@Param("roomId") Long roomId,
                                  @Param("monday") LocalDate monday,
                                  @Param("sunday") LocalDate sunday);
}
