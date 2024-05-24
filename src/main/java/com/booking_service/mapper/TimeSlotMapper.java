package com.booking_service.mapper;

import com.booking_service.model.dto.TimeSlotDTO;
import com.booking_service.model.entity.TimeSlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TimeSlotMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    TimeSlot dtoToEntity(TimeSlotDTO slotDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    TimeSlotDTO entityToDto(TimeSlot timeSlot);
}
