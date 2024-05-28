package com.booking_service.mapper;

import com.booking_service.model.dto.TimeSlotDTO;
import com.booking_service.model.entity.TimeSlot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TimeSlotMapper {

    TimeSlot toEntity(TimeSlotDTO slotDTO);
    TimeSlotDTO toDTO(TimeSlot timeSlot);

}
