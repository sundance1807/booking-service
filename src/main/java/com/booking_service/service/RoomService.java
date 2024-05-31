package com.booking_service.service;

import com.booking_service.exception.CustomException;
import com.booking_service.mapper.RoomMapper;
import com.booking_service.model.dto.RoomDTO;
import com.booking_service.model.entity.Room;
import com.booking_service.repository.RoomRepository;
import com.booking_service.util.MessageSource;
import com.booking_service.util.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoomService {

    private final RoomMapper roomMapper;
    private final RoomRepository roomRepository;

    public RoomDTO saveOne(RoomDTO roomDTO) {
        String roomName = StringUtil.toLowerCaseAndTrim(roomDTO.getName());
        Optional<Room> optionalRoom = roomRepository.findByName(roomName);

        if (optionalRoom.isPresent()) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.ROOM_NAME_ALREADY_EXISTS.getText(roomName))
                    .build();
        }

        Room room = roomMapper.toEntity(roomDTO);
        room.setName(roomName);
        room = roomRepository.save(room);

        return roomMapper.toDTO(room);
    }

    public RoomDTO getOne(Long id) {
        return roomMapper.toDTO(findById(id));
    }

    public void deleteOne(Long id) {
        Room room = findById(id);
        roomRepository.delete(room);
    }

    private Room findById(Long id) {
        return roomRepository.findById(id).orElseThrow(() -> CustomException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageSource.ROOM_NAME_NOT_FOUND.getText(id.toString()))
                .build());
    }

    public List<RoomDTO> getAll() {
        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toDTO)
                .toList();
    }

    public RoomDTO updateOne(Long id, RoomDTO roomDTO) {
        Room existingRoom = findById(id);
        existingRoom.setName(roomDTO.getName());
        existingRoom.setFloor(roomDTO.getFloor());
        existingRoom.setCapacity(roomDTO.getCapacity());
        Room updatedRoom = roomRepository.save(existingRoom);
        return roomMapper.toDTO(updatedRoom);
    }

    public void checkRoomExistsById(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.ROOM_NOT_FOUND.getText(roomId.toString()))
                    .build();
        }
    }

    public Room getOneEntity(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> CustomException.builder()
                        .message(MessageSource.ROOM_NOT_FOUND.getText(id.toString()))
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .build());
    }
}
