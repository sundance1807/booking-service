package com.booking_service.service;

import com.booking_service.exception.CustomException;
import com.booking_service.model.dto.RegistrationDTO;
import com.booking_service.model.entity.User;
import com.booking_service.repository.UserRepository;
import com.booking_service.util.MessageSource;
import com.booking_service.util.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> CustomException.builder()
                        .message(MessageSource.USER_NOT_FOUND.getText())
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .build());
    }

    @Transactional
    public String createUser(RegistrationDTO registrationDTO) {
        String username = StringUtil.toLowerCaseAndTrim(registrationDTO.getUsername());
        String formatedTelegramLink = StringUtil.formatTelegramLink(registrationDTO.getTelegramLink());
        checkUniqTelegramLink(formatedTelegramLink);
        checkUniqUsername(username);

        User entity = new User();
        entity.setUsername(username);
        entity.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        entity.setTelegramLink(formatedTelegramLink);
        entity.setFirstName(registrationDTO.getFirstName());
        entity.setLastName(registrationDTO.getLastName());
        userRepository.save(entity);

        return MessageSource.SUCCESS_REGISTRATION.getText();
    }

    private void checkUniqUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.USERNAME_ALREADY_EXISTS.getText(username))
                    .build();
        }
    }

    private void checkUniqTelegramLink(String telegramLink) {
        Optional<User> user = userRepository.findByTelegramLink(telegramLink);

        if (user.isPresent()) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.TELEGRAM_LINK_ALREADY_EXISTS.getText(telegramLink))
                    .build();
        }
    }
}
