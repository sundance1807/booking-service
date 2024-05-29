package com.booking_service.controller;

import com.booking_service.exception.CustomException;
import com.booking_service.model.dto.BookingDTO;
import com.booking_service.model.dto.WeekBookingDTO;
import com.booking_service.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * @param dto includes roomId and current date
     * @return map of bookings sorted by date from monday to sunday
     * @throws CustomException if room not found
     */
    @Operation(summary = "Получить все брони за текущую неделю.")
    @ApiResponse(responseCode = "200", description = "Метод для получения всех броней за текущую неделю.")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Map<LocalDate, List<BookingDTO>> getWeekBooking(@RequestBody WeekBookingDTO dto) throws CustomException {
        log.info("Incoming request to get all booking by weekday.");
        return bookingService.getWeekBookings(dto);
    }

    /**
     * @param id booking id to delete
     * @throws CustomException if room not found,
     *                         if unable to delete other booking;
     */
    @Operation(summary = "Удалить бронь по ID.")
    @ApiResponse(responseCode = "204", description = "Метод для удаления брони по ID.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOne(@PathVariable Long id) throws CustomException {
        log.info("Incoming request to delete booking by id: {}", id);
        bookingService.deleteOne(id);
    }
}
