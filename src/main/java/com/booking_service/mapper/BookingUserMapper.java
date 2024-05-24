package com.booking_service.mapper;

import com.booking_service.model.dto.BookingUserDTO;
import com.booking_service.model.entity.BookingUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingUserMapper {

    BookingUser toEntity(BookingUserDTO dto);

    BookingUserDTO toDTO(BookingUser entity);
}
