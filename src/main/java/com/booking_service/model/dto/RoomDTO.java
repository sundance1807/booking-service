package com.booking_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoomDTO {

    Long id;
    @NotBlank(message = "Поле 'name' не может быть пустым")
    String name;
    Integer floor;
    Integer capacity;

}
