package com.booking_service.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WeekBookingDTO {

    @Min(value = 1, message = "Поле 'roomId' должно быть больше 0.")
    Long roomId;
    @NotNull(message = "Поле 'date' не может быть пустым.")
    LocalDate date;
}
