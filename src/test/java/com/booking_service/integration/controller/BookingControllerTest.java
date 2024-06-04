package com.booking_service.integration.controller;

import com.booking_service.integration.config.SpringBootTestContainers;
import com.booking_service.model.dto.BookingDTO;
import com.booking_service.model.dto.ErrorResponseDTO;
import com.booking_service.model.entity.Booking;
import com.booking_service.model.entity.Room;
import com.booking_service.model.entity.User;
import com.booking_service.repository.BookingRepository;
import com.booking_service.repository.RoomRepository;
import com.booking_service.repository.UserRepository;
import com.booking_service.security.UserDetailsImpl;
import com.booking_service.util.MessageSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTestContainers
class BookingControllerTest {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void afterEach() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void saveOne_returnsBadRequest_whenNullValidationFailed() throws Exception {
        // given
        BookingDTO bookingDTO = new BookingDTO();
        String requestBody = objectMapper.writeValueAsString(bookingDTO);
        // when
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest()).andReturn();
        // then
        ErrorResponseDTO errorResponseDTO = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(StandardCharsets.UTF_8), ErrorResponseDTO.class);
        Map<String, String> invalidFields = errorResponseDTO.getInvalidFields();

        assertEquals(4, invalidFields.size());
        assertTrue(invalidFields.containsKey("title"));
        assertTrue(invalidFields.containsKey("startTime"));
        assertTrue(invalidFields.containsKey("endTime"));
        assertTrue(invalidFields.containsKey("roomId"));
        assertEquals("Поле 'title' не может быть пустым", invalidFields.get("title"));
        assertEquals("Поле 'startTime' не может быть пустым", invalidFields.get("startTime"));
        assertEquals("Поле 'endTime' не может быть пустым", invalidFields.get("endTime"));
        assertEquals("Поле 'roomId' не может быть пустым", invalidFields.get("roomId"));
    }

    @Test
    void saveOne_returnsBadRequest_whenMinValueValidationFailed() throws Exception {
        // given
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(-100L);
        bookingDTO.setTitle("");
        bookingDTO.setStartTime(LocalDateTime.now());
        bookingDTO.setEndTime(LocalDateTime.now());
        bookingDTO.setRoomId(-200L);
        String requestBody = objectMapper.writeValueAsString(bookingDTO);
        // when
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest()).andReturn();
        // then
        ErrorResponseDTO errorResponseDTO = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(StandardCharsets.UTF_8), ErrorResponseDTO.class);
        Map<String, String> invalidFields = errorResponseDTO.getInvalidFields();

        assertEquals(3, invalidFields.size());
        assertTrue(invalidFields.containsKey("id"));
        assertTrue(invalidFields.containsKey("title"));
        assertTrue(invalidFields.containsKey("roomId"));
        assertEquals("Поле 'id' должно быть больше 0", invalidFields.get("id"));
        assertEquals("Поле 'title' не может быть пустым", invalidFields.get("title"));
        assertEquals("Поле 'roomId' должно быть больше 0", invalidFields.get("roomId"));
    }

    @Test
    void saveOne_throwException_whenExistingOverlappingBookings() throws Exception {
        // given
        Booking booking = Instancio.create(Booking.class);
        LocalDateTime start = LocalDateTime.of(2024, 2, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 2, 1, 12, 0);
        booking.setStartTime(start);
        booking.setEndTime(end);

        booking = bookingRepository.save(booking);

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setRoomId(booking.getRoom().getId());
        bookingDTO.setStartTime(start);
        bookingDTO.setEndTime(end);
        bookingDTO.setTitle("1");
        bookingDTO.setId(1L);

        String requestBody = objectMapper.writeValueAsString(bookingDTO);
        // when
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest()).andReturn();
        // then
        ErrorResponseDTO errorResponseDTO = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(StandardCharsets.UTF_8), ErrorResponseDTO.class);

        assertEquals(MessageSource.BOOKING_TIME_NOT_AVAILABLE.getText(), errorResponseDTO.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponseDTO.getCode());
    }

    @Test
    void saveOne_throwException_whenRoomNotFound() throws Exception {
        // given
        BookingDTO bookingDTO = Instancio.create(BookingDTO.class);
        String requestBody = objectMapper.writeValueAsString(bookingDTO);
        // when
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound()).andReturn();
        // then
        ErrorResponseDTO errorResponseDTO = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(StandardCharsets.UTF_8), ErrorResponseDTO.class);

        assertEquals(MessageSource.ROOM_NOT_FOUND.getText(bookingDTO.getRoomId().toString()), errorResponseDTO.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponseDTO.getCode());
    }

    @Test
    void saveOne_throwException_whenUserNotFound() throws Exception {
        // given
        Room room = roomRepository.save(Instancio.create(Room.class));
        BookingDTO bookingDTO = Instancio.create(BookingDTO.class);
        bookingDTO.setRoomId(room.getId());

        UserDetailsImpl userDetails = Instancio.create(UserDetailsImpl.class);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        String requestBody = objectMapper.writeValueAsString(bookingDTO);
        // when
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound()).andReturn();
        // then
        ErrorResponseDTO errorResponseDTO = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(StandardCharsets.UTF_8), ErrorResponseDTO.class);

        assertEquals(MessageSource.USER_NOT_FOUND.getText(userDetails.getUsername()), errorResponseDTO.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponseDTO.getCode());
    }

    @Test
    void saveOne_returnsBookingDTO() throws Exception {
        // given
        Room room = roomRepository.save(Instancio.create(Room.class));
        User user = userRepository.save(Instancio.create(User.class));

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setRoomId(room.getId());
        bookingDTO.setStartTime(LocalDateTime.of(2024, 2, 1, 10, 0));
        bookingDTO.setEndTime(LocalDateTime.of(2024, 2, 1, 12, 0));
        bookingDTO.setTitle("title");
        bookingDTO.setDescription("description");

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        String requestBody = objectMapper.writeValueAsString(bookingDTO);
        // when
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk()).andReturn();
        // then
        String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        BookingDTO savedDTO = objectMapper.readValue(contentAsString, BookingDTO.class);

        assertNotNull(savedDTO.getId());
        assertEquals(bookingDTO.getTitle(), savedDTO.getTitle());
        assertEquals(bookingDTO.getDescription(), savedDTO.getDescription());
        assertEquals(bookingDTO.getStartTime(), savedDTO.getStartTime());
        assertEquals(bookingDTO.getEndTime(), savedDTO.getEndTime());
        assertEquals(bookingDTO.getRoomId(), savedDTO.getRoomId());
    }

    @Test
    void deleteOne_throwException_whenBookingNotFound() throws Exception {
        Long bookingId = 1L;
        // when
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound()).andReturn();
        // then
        ErrorResponseDTO errorResponseDTO = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(StandardCharsets.UTF_8), ErrorResponseDTO.class);

        assertEquals(MessageSource.BOOKING_NOT_FOUND.getText(bookingId.toString()), errorResponseDTO.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponseDTO.getCode());
    }

    @Test
    void deleteOne_throwException_whenUserNotAuthorized() throws Exception {
        // given
        User user = Instancio.create(User.class);
        user.setUsername("user");
        user = userRepository.save(user);
        Booking booking = Instancio.create(Booking.class);
        booking.setUser(user);
        booking = bookingRepository.save(booking);

        User anotherUser = Instancio.create(User.class);
        anotherUser.setUsername("another_user");
        anotherUser = userRepository.save(anotherUser);

        UserDetailsImpl userDetails = new UserDetailsImpl(anotherUser);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        // when
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/bookings/{id}", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isForbidden()).andReturn();
        // then
        ErrorResponseDTO errorResponseDTO = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(StandardCharsets.UTF_8), ErrorResponseDTO.class);

        assertEquals(MessageSource.UNABLE_DELETE_OTHER_BOOKINGS.getText(), errorResponseDTO.getMessage());
        assertEquals(HttpStatus.FORBIDDEN.value(), errorResponseDTO.getCode());
    }

    @Test
    void deleteOne_deletesBooking() throws Exception {
        // given
        User user = userRepository.save(Instancio.create(User.class));
        Booking booking = Instancio.create(Booking.class);
        booking.setUser(user);
        booking = bookingRepository.save(booking);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        // when
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/bookings/{id}", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isNoContent());

        assertFalse(bookingRepository.existsById(booking.getId()));

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/bookings/{id}", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound()).andReturn();
        // then
        ErrorResponseDTO errorResponseDTO = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(StandardCharsets.UTF_8), ErrorResponseDTO.class);

        assertEquals(MessageSource.BOOKING_NOT_FOUND.getText(booking.getId().toString()), errorResponseDTO.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponseDTO.getCode());
    }
}
