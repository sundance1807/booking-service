package com.booking_service.service;

import com.booking_service.exception.CustomException;
import com.booking_service.mapper.BookingMapper;
import com.booking_service.model.dto.BookingDTO;
import com.booking_service.model.dto.WeekBookingDTO;
import com.booking_service.model.entity.Booking;
import com.booking_service.model.entity.Room;
import com.booking_service.model.entity.User;
import com.booking_service.repository.BookingRepository;
import com.booking_service.security.service.JwtService;
import com.booking_service.util.MessageSource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final UserService userService;

    public Map<LocalDate, List<BookingDTO>> getWeekBookings(WeekBookingDTO dto) {
        roomService.checkRoomExistsById(dto.getRoomId());
        String username = jwtService.getUsername();
        LocalDate date = dto.getDate();
        LocalDateTime startOfWeek = date.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime endOfWeek = date.with(DayOfWeek.SUNDAY).atTime(LocalTime.MAX);

        List<Booking> weekBookings = bookingRepository.getWeekBookings(dto.getRoomId(), startOfWeek, endOfWeek);

        return weekBookings.stream()
                .map(booking -> {
                    BookingDTO bookingDTO = bookingMapper.toDTO(booking);
                    bookingDTO.setEditable(booking.getUser().getUsername().equals(username));

                    return bookingDTO;
                })
                .sorted(Comparator.comparing(BookingDTO::getStartTime))
                .collect(Collectors.groupingBy(t -> t.getStartTime().toLocalDate()));
    }


    @Transactional
    public BookingDTO saveOne(BookingDTO bookingDTO) {
        boolean existsOverlappingBookings = bookingRepository.existsOverlappingBookings(bookingDTO.getRoomId(), bookingDTO.getStartTime(), bookingDTO.getEndTime());
        if (existsOverlappingBookings) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.BOOKING_TIME_NOT_AVAILABLE.getText())
                    .build();
        }

        Booking entity = bookingMapper.toEntity(bookingDTO);
        Room room = roomService.getOneEntity(bookingDTO.getRoomId());
        String username = jwtService.getUsername();
        User user = userService.getEntityByUsername(username);

        entity.setRoom(room);
        entity.setUser(user);
        entity = bookingRepository.save(entity);

        return bookingMapper.toDTO(entity);
    }

    public void deleteOne(Long id) {
        String username = jwtService.getUsername();
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .message(MessageSource.BOOKING_NOT_FOUND.getText(id.toString()))
                        .build());

        if (!booking.getUser().getUsername().equals(username)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message(MessageSource.UNABLE_DELETE_OTHER_BOOKINGS.getText())
                    .build();
        } else {
            bookingRepository.delete(booking);
        }
    }
}
