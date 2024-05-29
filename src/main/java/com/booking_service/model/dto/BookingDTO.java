package com.booking_service.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private Long roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean editable;
}
