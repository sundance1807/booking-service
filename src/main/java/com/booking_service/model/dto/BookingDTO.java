package com.booking_service.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class BookingDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private Long roomId;
    private Set<TimeSlotDTO> timeSlots;
    private Boolean editable;
}
