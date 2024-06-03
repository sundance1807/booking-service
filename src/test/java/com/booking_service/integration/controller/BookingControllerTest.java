package com.booking_service.integration.controller;

import com.booking_service.integration.config.SpringBootTestContainers;
import com.booking_service.model.dto.BookingDTO;
import com.booking_service.model.dto.ErrorResponseDTO;
import com.booking_service.model.entity.Booking;
import com.booking_service.repository.BookingRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTestContainers
class BookingControllerTest {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookingRepository bookingRepository;

    @AfterEach
    void afterEach() {
        bookingRepository.deleteAll();
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
        assertEquals("Поле 'id' не может быть меньше 1", invalidFields.get("id"));
        assertEquals("Поле 'title' не может быть пустым", invalidFields.get("title"));
        assertEquals("Поле 'roomId' не может быть меньше 1", invalidFields.get("roomId"));
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
}
