package com.booking_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoomDTO {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;
    @NotBlank(message = "Поле 'name' не может быть пустым")
    String name;
    String floor;
    String capacity;

}
