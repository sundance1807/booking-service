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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

        when(bookingRepository.existsOverlappingBookings(dto.getRoomId(), dto.getStartTime(), dto.getEndTime())).thenReturn(true);
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
        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        BookingDTO bookingDTO = Instancio.create(BookingDTO.class);
        Room room = Instancio.create(Room.class);
        User user = Instancio.create(User.class);
        String username = "username";
        Booking bookingEntity = bookingMapper.toEntity(bookingDTO);

        when(bookingRepository.existsOverlappingBookings(bookingDTO.getRoomId(), bookingDTO.getStartTime(), bookingDTO.getEndTime())).thenReturn(false);
        when(roomService.getOneEntity(bookingDTO.getRoomId())).thenReturn(room);
        when(jwtService.getUsername()).thenReturn(username);
        when(userService.getEntityByUsername(username)).thenReturn(user);
        when(bookingRepository.save(any())).thenReturn(bookingEntity);
        //when
        BookingDTO result = underTest.saveOne(bookingDTO);
        //then
        assertNotNull(result);
        verify(bookingRepository).save(captor.capture());
        Booking savedEntity = captor.getValue();
        assertEquals(bookingDTO.getTitle(), savedEntity.getTitle());
        assertEquals(bookingDTO.getDescription(), savedEntity.getDescription());
        assertEquals(bookingDTO.getStartTime(), savedEntity.getStartTime());
        assertEquals(bookingDTO.getEndTime(), savedEntity.getEndTime());
        assertEquals(room, savedEntity.getRoom());
        assertEquals(user, savedEntity.getUser());
    }
}
