package com.booking_service.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WeekBookingDTO {

    Long roomId;
    LocalDate date;
}
