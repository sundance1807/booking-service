package com.booking_service.controller;

import com.booking_service.exception.CustomException;
import com.booking_service.model.dto.RoomDTO;
import com.booking_service.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/rooms/v1")
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
    public RoomDTO saveOne(@RequestBody RoomDTO roomDTO) throws CustomException {
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
    public RoomDTO getOne(@PathVariable Long id) throws CustomException {
        log.info("Incoming request to get one room: {}", id);
        return roomService.getOne(id);
    }

    /**
     * @param id of room
     * @throws CustomException if there is no room with such id
     */
    @Operation(summary = "Удаление комнаты.")
    @ApiResponse(responseCode = "200", description = "Метод для удаления комнаты.")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOne(@PathVariable Long id) throws CustomException {
        log.info("Incoming request to delete room: {}", id);
        roomService.deleteOne(id);
    }

}
