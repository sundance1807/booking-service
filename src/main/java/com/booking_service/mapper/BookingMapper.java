package com.booking_service.mapper;

import com.booking_service.model.dto.BookingDTO;
import com.booking_service.model.entity.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toEntity(BookingDTO dto);
    BookingDTO toDTO(Booking entity);
}
