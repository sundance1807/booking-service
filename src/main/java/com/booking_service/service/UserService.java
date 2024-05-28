package com.booking_service.service;

import com.booking_service.exception.CustomException;
import com.booking_service.mapper.UserMapper;
import com.booking_service.model.entity.User;
import com.booking_service.repository.UserRepository;
import com.booking_service.util.MessageSource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User getEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> CustomException.builder()
                        .message(MessageSource.USER_NOT_FOUND.getText())
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .build());
    }
}
