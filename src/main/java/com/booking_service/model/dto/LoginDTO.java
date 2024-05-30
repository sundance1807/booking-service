package com.booking_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "Поле 'username' не может быть пустым.")
    private String username;
    @NotBlank(message = "Поле 'password' не может быть пустым.")
    private String password;
}
