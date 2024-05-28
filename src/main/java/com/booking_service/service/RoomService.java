package com.booking_service.service;

import com.booking_service.exception.CustomException;
import com.booking_service.repository.RoomRepository;
import com.booking_service.util.MessageSource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public void checkRoomExistsById(Long roomId) throws CustomException {
        if (!roomRepository.existsById(roomId)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.ROOM_NOT_FOUND.getText(roomId.toString()))
                    .build();
        }
    }
}




