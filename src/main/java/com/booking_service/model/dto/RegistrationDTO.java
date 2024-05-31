package com.booking_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@ToString(exclude = "password")
@Data
public class RegistrationDTO {

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
