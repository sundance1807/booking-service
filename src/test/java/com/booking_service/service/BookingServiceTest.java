package com.booking_service.service;

import com.booking_service.exception.CustomException;
import com.booking_service.mapper.BookingMapper;
import com.booking_service.mapper.BookingMapperImpl;
import com.booking_service.model.dto.BookingDTO;
import com.booking_service.model.entity.Booking;
import com.booking_service.model.entity.Room;
import com.booking_service.model.entity.User;
import com.booking_service.repository.BookingRepository;
import com.booking_service.security.service.JwtService;
import com.booking_service.util.MessageSource;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RoomService roomService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserService userService;
    @InjectMocks
    private BookingService underTest;

    @Test
    void saveOne_throwException_whenTimeRangeNotAvailable() {
        // given
        BookingDTO dto = Instancio.create(BookingDTO.class);
        List<Booking> bookings = Instancio.createList(Booking.class);

        when(bookingRepository.findOverlappingBookings(dto.getRoomId(), dto.getStartTime(), dto.getEndTime())).thenReturn(bookings);
        //when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.saveOne(dto));
        //then
        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(MessageSource.BOOKING_TIME_NOT_AVAILABLE.getText(), exception.getMessage());
    }

    @Test
    void saveOne_returnBookingDTO() {
        // given
        ArgumentCaptor<Booking> bookingArgumentCaptor = ArgumentCaptor.forClass(Booking.class);

        BookingDTO dto = Instancio.create(BookingDTO.class);
        Room room = Instancio.create(Room.class);
        User user = Instancio.create(User.class);
        String username = "username";

        when(bookingRepository.findOverlappingBookings(dto.getRoomId(), dto.getStartTime(), dto.getEndTime())).thenReturn(Collections.emptyList());
        when(roomService.getOneEntity(dto.getRoomId())).thenReturn(room);
        when(jwtService.getUsername()).thenReturn(username);
        when(userService.getEntityByUsername(username)).thenReturn(user);
        //when
        BookingDTO result = underTest.saveOne(dto);
        //then
        assertNotNull(result);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
    }
}
