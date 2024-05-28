package com.booking_service.controller;

import com.booking_service.exception.CustomException;
import com.booking_service.model.dto.AuthResponseDTO;
import com.booking_service.model.dto.LoginDTO;
import com.booking_service.model.dto.RegistrationDTO;
import com.booking_service.security.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    /**
     *
     * @param registrationDTO user to be created
     * @return newly registered user
     * @throws CustomException if there duplicated username
     */
    @PostMapping("/registration")
    public String registration(@RequestBody RegistrationDTO registrationDTO) {
        log.info("Incoming registration request: {}.", registrationDTO); // TODO sensitive data log

        return authService.createUser(registrationDTO);
    }

    /**
     *
     * @param loginDTO user to be login
     * @return token
     */
    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginDTO loginDTO) {
        log.info("Incoming login request from: {}.", loginDTO.getUsername()); // TODO sensitive data log

        return authService.login(loginDTO);
    }
}
