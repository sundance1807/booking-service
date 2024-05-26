package com.booking_service.mapper;

import com.booking_service.model.dto.UserDTO;
import com.booking_service.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDTO dto);

    UserDTO toDTO(User entity);
}
