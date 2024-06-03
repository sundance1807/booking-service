package com.booking_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDTO {

    @Min(value = 1, message = "Поле 'id' должно быть больше 0")
    private Long id;
    @NotBlank(message = "Поле 'title' не может быть пустым")
    private String title;
    private String description;
    @NotNull(message = "Поле 'startTime' не может быть пустым")
    private LocalDateTime startTime;
    @NotNull(message = "Поле 'endTime' не может быть пустым")
    private LocalDateTime endTime;
    @Min(value = 1, message = "Поле 'roomId' должно быть больше 0")
    @NotNull(message = "Поле 'roomId' не может быть пустым")
    private Long roomId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean editable;
}
