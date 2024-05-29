package com.booking_service.mapper;

import com.booking_service.model.dto.RoomDTO;
import com.booking_service.model.entity.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    Room toEntity(RoomDTO roomDTO);
    RoomDTO toDTO(Room room);

}
