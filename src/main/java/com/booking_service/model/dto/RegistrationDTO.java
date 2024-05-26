package com.booking_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrationDTO { // TODO RegistrationDTO same as UserDto

    @NotBlank(message = "Поле 'username' не может быть пустым.")
    private String username;
    @NotBlank(message = "Поле 'password' не может быть пустым.")
    private String password;
    @NotBlank(message = "Поле 'telegramLink' не может быть пустым.")
    private String telegramLink;
    @NotBlank(message = "Поле 'firstName' не может быть пустым.")
    private String firstName;
    @NotBlank(message = "Поле 'lastName' не может быть пустым.")
    private String lastName;
}
