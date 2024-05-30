package com.booking_service.mapper;

import com.booking_service.model.dto.BookingDTO;
import com.booking_service.model.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "room", ignore = true)
    @Mapping(target = "user", ignore = true)
    Booking toEntity(BookingDTO dto);

    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "editable", ignore = true)
    BookingDTO toDTO(Booking entity);
}
