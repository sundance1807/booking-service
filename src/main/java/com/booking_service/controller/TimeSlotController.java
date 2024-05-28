package com.booking_service.controller;

import com.booking_service.exception.CustomException;
import com.booking_service.model.dto.TimeSlotDTO;
import com.booking_service.service.TimeSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/v1/slots")
public class TimeSlotController {

    private TimeSlotService timeSlotService;

    /**
     * @param timeSlotDTO time slot to be created
     * @return newly created time slot with proper {@link HttpStatus}
     * @throws CustomException if there is duplicated time slot name
     */
    @Operation(summary = "Создать временной слот.")
    @ApiResponse(responseCode = "200", description = "Метод для создания временного слота.")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public TimeSlotDTO saveOne(@RequestBody TimeSlotDTO timeSlotDTO) throws CustomException {
        log.info("Incoming request to save time slot: {}", timeSlotDTO);
        return timeSlotService.saveOne(timeSlotDTO);
    }

    /**
     *
     * @param id of time slot
     * @return existing time slot with proper id
     * @throws CustomException if there is no time slot with such id
     */
    @Operation(summary = "Получение временного слота.")
    @ApiResponse(responseCode = "200", description = "Метод для получения временного слота.")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TimeSlotDTO getOne(@PathVariable Long id) throws CustomException {
        log.info("Incoming request to get time slot: {}", id);
        return timeSlotService.getOne(id);
    }

    /**
     * @param id of time slot
     * @throws CustomException if there is no time slot with such id
     */
    @Operation(summary = "Удаление временного слота.")
    @ApiResponse(responseCode = "200", description = "Метод для удаления временного слота.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOne(@PathVariable Long id) throws CustomException {
        log.info("Incoming request to delete time slot: {}", id);
        timeSlotService.deleteOne(id);
    }

}
