package com.booking_service.controller;

import com.booking_service.exception.CustomException;
import com.booking_service.model.dto.RoomDTO;
import com.booking_service.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/v1/rooms")
public class RoomController {

    private RoomService roomService;

    /**
     * @param roomDTO room to be created
     * @return newly created room with proper {@link HttpStatus}
     * @throws CustomException if there is duplicated room name
     */
    @Operation(summary = "Создать комнату.")
    @ApiResponse(responseCode = "200", description = "Метод для создания комнаты.")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public RoomDTO saveOne(@RequestBody @Valid RoomDTO roomDTO) {
        log.info("Incoming request to save room: {}", roomDTO);
        return roomService.saveOne(roomDTO);
    }

    /**
     * @param id of room
     * @return existing room with proper id
     * @throws CustomException if there is no room with such id
     */
    @Operation(summary = "Получение комнаты.")
    @ApiResponse(responseCode = "200", description = "Метод для получения комнаты.")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoomDTO getOne(@PathVariable Long id) {
        log.info("Incoming request to get one room: {}", id);
        return roomService.getOne(id);
    }

    /**
     * @param id of room
     * @throws CustomException if there is no room with such id
     */
    @Operation(summary = "Удаление комнаты.")
    @ApiResponse(responseCode = "200", description = "Метод для удаления комнаты.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOne(@PathVariable Long id) {
        log.info("Incoming request to delete room: {}", id);
        roomService.deleteOne(id);
    }

    /**
     * @return list of all rooms
     */
    @Operation(summary = "Получение всех комнат.")
    @ApiResponse(responseCode = "200", description = "Метод для получения всех комнат.")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RoomDTO> getAll() {
        log.info("Incoming request to getAll rooms");
        return roomService.getAll();
    }

    /**
     * @param id the ID of the room to be updated
     * @param roomDTO the data to update the room
     * @return the updated room data
     * @throws CustomException if the room with the specified ID is not found
     */
    @Operation(summary = "Обновление комнаты.")
    @ApiResponse(responseCode = "200", description = "Метод для обновления комнаты.")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoomDTO updateOne(@PathVariable Long id, @RequestBody @Valid RoomDTO roomDTO) {
        log.info("Incoming request to update room with id {}: {}", id, roomDTO);
        return roomService.updateOne(id, roomDTO);
    }

}
