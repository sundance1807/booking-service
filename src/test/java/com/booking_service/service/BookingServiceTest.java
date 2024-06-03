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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    void saveOne_throwException_whenRoomNotFound() {
        // given
        BookingDTO dto = Instancio.create(BookingDTO.class);
        Long roomId = dto.getRoomId();

        when(bookingRepository.existsOverlappingBookings(roomId, dto.getStartTime(), dto.getEndTime())).thenReturn(false);
        when(roomService.getOneEntity(roomId)).thenThrow(CustomException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageSource.ROOM_NOT_FOUND.getText(roomId.toString()))
                .build());
        //when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.saveOne(dto));
        //then
        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(MessageSource.ROOM_NOT_FOUND.getText(roomId.toString()), exception.getMessage());
    }

    @Test
    void saveOne_throwException_whenUserNotFound() {
        // given
        BookingDTO dto = Instancio.create(BookingDTO.class);
        String username = "username";

        when(bookingRepository.existsOverlappingBookings(dto.getRoomId(), dto.getStartTime(), dto.getEndTime())).thenReturn(false);
        when(roomService.getOneEntity(dto.getRoomId())).thenReturn(Instancio.create(Room.class));
        when(jwtService.getUsername()).thenReturn(username);
        when(userService.getEntityByUsername(any())).thenThrow(CustomException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageSource.USER_NOT_FOUND.getText(username))
                .build());
        //when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.saveOne(dto));
        //then
        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(MessageSource.USER_NOT_FOUND.getText(username), exception.getMessage());
    }

    @Test
    void updateOne_throwException_whenBookingNotFound() {
        // given
        String username = "username";
        BookingDTO bookingDTO = Instancio.create(BookingDTO.class);

        when(jwtService.getUsername()).thenReturn(username);
        when(bookingRepository.findById(any())).thenReturn(Optional.empty());
        // when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.updateOne(bookingDTO));
        // then
        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(MessageSource.BOOKING_NOT_FOUND.getText(bookingDTO.getId().toString()), exception.getMessage());
    }

    @Test
    void updateOne_throwException_whenUserHasNoAccessToUpdateBooking() {
        // given
        String username = "username";
        BookingDTO bookingDTO = Instancio.create(BookingDTO.class);
        Booking booking = Instancio.create(Booking.class);
        User user = Instancio.create(User.class);
        user.setUsername("anotherUsername");
        booking.setUser(user);

        when(jwtService.getUsername()).thenReturn(username);
        when(bookingRepository.findById(bookingDTO.getId())).thenReturn(Optional.of(booking));
        // when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.updateOne(bookingDTO));
        // then
        assertNotNull(exception);
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
        assertEquals(MessageSource.UNABLE_UPDATE_OTHER_BOOKINGS.getText(), exception.getMessage());
    }

    @Test
    void updateOne_throwException_whenTimeRangeNotAvailable() {
        // given
        String username = "username";
        BookingDTO bookingDTO = Instancio.create(BookingDTO.class);
        Booking booking = Instancio.create(Booking.class);
        booking.setId(bookingDTO.getId());
        booking.setStartTime(bookingDTO.getStartTime());
        booking.setEndTime(bookingDTO.getEndTime());
        booking.setUser(Instancio.create(User.class));
        booking.getUser().setUsername(username);

        Booking overlappingBooking = Instancio.create(Booking.class);
        overlappingBooking.setId(bookingDTO.getId() + 1);
        overlappingBooking.setStartTime(bookingDTO.getStartTime());
        overlappingBooking.setEndTime(bookingDTO.getEndTime());

        when(jwtService.getUsername()).thenReturn(username);
        when(bookingRepository.findById(bookingDTO.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.getOverlappingBookings(bookingDTO.getRoomId(), bookingDTO.getStartTime(), bookingDTO.getEndTime())).thenReturn(List.of(overlappingBooking));
        // when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.updateOne(bookingDTO));
        // then
        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(MessageSource.BOOKING_TIME_NOT_AVAILABLE.getText(), exception.getMessage());
    }

    @Test
    void updateOne_returnBookingDTO_whenDifferentRoom() {
        // given
        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        String username = "username";
        BookingDTO bookingDTO = Instancio.create(BookingDTO.class);
        Booking booking = Instancio.create(Booking.class);
        booking.setId(bookingDTO.getId());
        booking.setUser(Instancio.create(User.class));
        booking.getUser().setUsername(username);

        Room room = Instancio.create(Room.class);
        booking.setRoom(room);
        Room anotherRoom = Instancio.create(Room.class);
        anotherRoom.setId(room.getId() + 1L);
        bookingDTO.setRoomId(anotherRoom.getId());

        when(jwtService.getUsername()).thenReturn(username);
        when(bookingRepository.findById(bookingDTO.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.getOverlappingBookings(bookingDTO.getRoomId(), bookingDTO.getStartTime(), bookingDTO.getEndTime())).thenReturn(List.of());
        when(bookingRepository.save(any())).thenReturn(booking);
        when(roomService.getOneEntity(bookingDTO.getRoomId())).thenReturn(anotherRoom);
        // when
        BookingDTO result = underTest.updateOne(bookingDTO);
        // then
        assertNotNull(result);
        assertEquals(bookingDTO.getTitle(), result.getTitle());
        assertEquals(bookingDTO.getDescription(), result.getDescription());
        assertEquals(bookingDTO.getStartTime(), result.getStartTime());
        assertEquals(bookingDTO.getEndTime(), result.getEndTime());

        verify(bookingRepository).save(captor.capture());
        Booking savedEntity = captor.getValue();
        assertEquals(bookingDTO.getTitle(), savedEntity.getTitle());
        assertEquals(bookingDTO.getDescription(), savedEntity.getDescription());
        assertEquals(bookingDTO.getStartTime(), savedEntity.getStartTime());
        assertEquals(bookingDTO.getEndTime(), savedEntity.getEndTime());
        assertEquals(bookingDTO.getRoomId(), savedEntity.getRoom().getId());
    }

    @Test
    void updateOne_returnBookingDTO_whenSameRoom() {
        // given
        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        String username = "username";
        BookingDTO bookingDTO = Instancio.create(BookingDTO.class);
        Booking booking = Instancio.create(Booking.class);
        booking.setId(bookingDTO.getId());
        booking.setUser(Instancio.create(User.class));
        booking.getUser().setUsername(username);

        Room room = Instancio.create(Room.class);
        booking.setRoom(room);
        bookingDTO.setRoomId(room.getId());

        when(jwtService.getUsername()).thenReturn(username);
        when(bookingRepository.findById(bookingDTO.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.getOverlappingBookings(bookingDTO.getRoomId(), bookingDTO.getStartTime(), bookingDTO.getEndTime())).thenReturn(List.of());
        when(bookingRepository.save(any())).thenReturn(booking);
        // when
        BookingDTO result = underTest.updateOne(bookingDTO);
        // then
        assertNotNull(result);
        assertEquals(bookingDTO.getTitle(), result.getTitle());
        assertEquals(bookingDTO.getDescription(), result.getDescription());
        assertEquals(bookingDTO.getStartTime(), result.getStartTime());
        assertEquals(bookingDTO.getEndTime(), result.getEndTime());

        verify(bookingRepository).save(captor.capture());
        Booking savedEntity = captor.getValue();
        assertEquals(bookingDTO.getTitle(), savedEntity.getTitle());
        assertEquals(bookingDTO.getDescription(), savedEntity.getDescription());
        assertEquals(bookingDTO.getStartTime(), savedEntity.getStartTime());
        assertEquals(bookingDTO.getEndTime(), savedEntity.getEndTime());
        assertEquals(bookingDTO.getRoomId(), savedEntity.getRoom().getId());

        verify(roomService, never()).getOneEntity(any());
    }
}
