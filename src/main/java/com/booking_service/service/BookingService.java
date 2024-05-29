package com.booking_service.service;

import com.booking_service.exception.CustomException;
import com.booking_service.mapper.BookingMapper;
import com.booking_service.model.dto.BookingDTO;
import com.booking_service.model.dto.WeekBookingDTO;
import com.booking_service.model.entity.Booking;
import com.booking_service.repository.BookingRepository;
import com.booking_service.security.service.JwtService;
import com.booking_service.util.MessageSource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final BookingMapper bookingMapper;
    private final JwtService jwtService;

    public Map<LocalDate, List<BookingDTO>> getWeekBookings(WeekBookingDTO dto) throws CustomException {
        roomService.checkRoomExistsById(dto.getRoomId());
        String username = jwtService.getUsername();
        LocalDate date = dto.getDate();
        List<Booking> weekBookings = bookingRepository.getWeekBookings(dto.getRoomId(), date.with(DayOfWeek.MONDAY), date.with(DayOfWeek.SUNDAY));

        return weekBookings.stream()
                .map(booking -> {
                    BookingDTO bookingDTO = bookingMapper.toDTO(booking);
                    bookingDTO.setEditable(booking.getUser().getUsername().equals(username));

                    return bookingDTO;
                })
                .sorted(Comparator.comparing(BookingDTO::getDate))
                .collect(Collectors.groupingBy(BookingDTO::getDate));
    }

    public void deleteOne(Long id) throws CustomException {
        String username = jwtService.getUsername();
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .message(MessageSource.BOOKING_NOT_FOUND.getText(id.toString()))
                        .build());

        if (!booking.getUser().getUsername().equals(username)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.UNABLE_DELETE_OTHER_BOOKINGS.getText())
                    .build();
        } else {
            bookingRepository.delete(booking);
        }
    }
}
