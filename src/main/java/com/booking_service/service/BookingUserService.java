package com.booking_service.service;

import com.booking_service.exception.CustomException;
import com.booking_service.model.dto.AuthResponseDTO;
import com.booking_service.model.dto.LoginDTO;
import com.booking_service.model.dto.RegistrationDTO;
import com.booking_service.model.entity.BookingUser;
import com.booking_service.repository.BookingUserRepository;
import com.booking_service.security.JwtGenerator;
import com.booking_service.util.MessageSource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BookingUserService {

    private AuthenticationManager authenticationManager;
    private BookingUserRepository bookingUserRepository;
    private PasswordEncoder passwordEncoder;
    private JwtGenerator jwtGenerator;

    public String saveOne(RegistrationDTO registrationDTO) throws CustomException {
        String username = registrationDTO.getUsername().trim().toLowerCase();
        Optional<BookingUser> optionalBookingUser = bookingUserRepository.findByUsername(username);

        if (optionalBookingUser.isPresent()) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message(MessageSource.USERNAME_ALREADY_EXISTS.getText(username))
                    .build();
        }

        BookingUser entity = new BookingUser();
        entity.setUsername(username);
        entity.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        entity.setTelegramLink(registrationDTO.getTelegramLink());
        entity.setFirstName(registrationDTO.getFirstName());
        entity.setSecondName(registrationDTO.getSecondName());
        bookingUserRepository.save(entity);

        return MessageSource.SUCCESS_REGISTRATION.getText();
    }

    public AuthResponseDTO loginOne(@RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);

        return new AuthResponseDTO(token);
    }
}
