package com.booking_service.security.service;

import com.booking_service.exception.CustomException;
import com.booking_service.model.dto.AuthResponseDTO;
import com.booking_service.model.dto.LoginDTO;
import com.booking_service.model.dto.RegistrationDTO;
import com.booking_service.model.entity.User;
import com.booking_service.repository.UserRepository;
import com.booking_service.util.MessageSource;
import com.booking_service.util.StringUtil;
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
public class AuthService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    public String createUser(RegistrationDTO registrationDTO) {
        String username = StringUtil.toLowerCaseAndTrim(registrationDTO.getUsername());
        String formatedTelegramLink = StringUtil.formatTelegramLink(registrationDTO.getTelegramLink());
        checkUniquenessTelegramLink(formatedTelegramLink);
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message(MessageSource.USERNAME_ALREADY_EXISTS.getText(username))
                    .build();
        }

        User entity = new User();
        entity.setUsername(username);
        entity.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        entity.setTelegramLink(formatedTelegramLink);
        entity.setFirstName(registrationDTO.getFirstName());
        entity.setLastName(registrationDTO.getLastName());
        userRepository.save(entity);

        return MessageSource.SUCCESS_REGISTRATION.getText();
    }

    public AuthResponseDTO login(@RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(authentication);

        return new AuthResponseDTO(token);
    }

    private void checkUniquenessTelegramLink(String telegramLink) {
        Optional<User> user = userRepository.findByTelegramLink(telegramLink);

        if (user.isPresent()) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.TELEGRAM_LINK_ALREADY_EXISTS.getText(telegramLink))
                    .build();
        }
    }
}
