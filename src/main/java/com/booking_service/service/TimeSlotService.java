package com.booking_service.service;

import com.booking_service.exception.CustomException;
import com.booking_service.mapper.TimeSlotMapper;
import com.booking_service.model.dto.TimeSlotDTO;
import com.booking_service.model.entity.TimeSlot;
import com.booking_service.repository.TimeSlotRepository;
import com.booking_service.util.MessageSource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TimeSlotService {

    private final TimeSlotMapper timeSlotMapper;
    private final TimeSlotRepository timeSlotRepository;

    public TimeSlotDTO saveOne(TimeSlotDTO timeSlotDTO) throws CustomException {
        String timeSlotName = timeSlotDTO.getName().toLowerCase().trim();
        Optional<TimeSlot> optionalTimeSlot = timeSlotRepository.findByName(timeSlotName);

        if (optionalTimeSlot.isPresent()) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.TIME_SLOT_ALREADY_EXISTS.getText(timeSlotName))
                    .build();
        }

        TimeSlot timeSlot = timeSlotMapper.toEntity(timeSlotDTO);
        timeSlot.setName(timeSlotName);
        timeSlot = timeSlotRepository.save(timeSlot);
        timeSlotDTO = timeSlotMapper.toDTO(timeSlot);

        return timeSlotDTO;
    }

    public TimeSlotDTO getOne(Long id) throws CustomException {
        return timeSlotMapper.toDTO(findById(id));
    }

    public void deleteOne(Long id) throws CustomException {
        TimeSlot timeSlot = findById(id);
        timeSlotRepository.delete(timeSlot);
    }

    private TimeSlot findById(Long id) throws CustomException {
        return timeSlotRepository.findById(id).orElseThrow(() ->
                CustomException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .message(MessageSource.TIME_SLOT_NOT_FOUND.getText(id.toString()))
                        .build());
    }

}
