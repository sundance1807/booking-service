package com.booking_service.service;

import com.booking_service.exception.CustomException;
import com.booking_service.mapper.TimeSlotMapper;
import com.booking_service.mapper.TimeSlotMapperImpl;
import com.booking_service.model.dto.TimeSlotDTO;
import com.booking_service.model.entity.TimeSlot;
import com.booking_service.repository.TimeSlotRepository;
import com.booking_service.util.MessageSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TimeSlotServiceTest {
    private static final Long ID = 1L;
    private static final String STRING = "timeslot";
    private TimeSlotDTO dto;
    @Mock
    private TimeSlotRepository timeSlotRepository;
    @InjectMocks
    private TimeSlotService underTest;

    @BeforeEach
    public void setUp() {
        TimeSlotMapper timeSlotMapper = new TimeSlotMapperImpl();
        underTest = new TimeSlotService(timeSlotMapper, timeSlotRepository);

        dto = new TimeSlotDTO();
        dto.setName(STRING);
    }

    @Test
    void saveOne_throwsException_whenNameIsDuplicated() {
        //given
        TimeSlot entity = new TimeSlot();
        when(timeSlotRepository.findByName(anyString())).thenReturn(Optional.of(entity));
        //when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.saveOne(dto));
        //then
        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(MessageSource.TIME_SLOT_ALREADY_EXISTS.getText(dto.getName()), exception.getMessage());
    }

    @Test
    void saveOne_savesTimeSlot() throws CustomException {
        //given
        ArgumentCaptor<TimeSlot> timeSlotArgumentCaptor = ArgumentCaptor.forClass(TimeSlot.class);
        TimeSlot entity = new TimeSlot();
        entity.setId(ID);
        entity.setName(STRING);
        when(timeSlotRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(timeSlotRepository.save(any())).thenReturn(entity);
        //when
        TimeSlotDTO result = underTest.saveOne(dto);
        //then
        assertNotNull(result);
        verify(timeSlotRepository).save(timeSlotArgumentCaptor.capture());
        TimeSlot savedTimeSlot = timeSlotArgumentCaptor.getValue();
        assertNotNull(result.getId());
        assertEquals(dto.getName(), savedTimeSlot.getName());
    }

    @Test
    void getOne_throwsException_whenTimeSlotNotFound() {
        //given
        when(timeSlotRepository.findById(ID)).thenReturn(Optional.empty());
        //when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.getOne(ID));
        //then
        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(MessageSource.TIME_SLOT_NOT_FOUND.getText(ID.toString()), exception.getMessage());
    }

    @Test
    void getOne_returnTimeSlot() throws CustomException {
        //given
        TimeSlot entity = new TimeSlot();
        entity.setId(ID);
        entity.setName(STRING);
        when(timeSlotRepository.findById(ID)).thenReturn(Optional.of(entity));
        //when
        TimeSlotDTO result = underTest.getOne(ID);
        //then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(ID, result.getId());
        assertNotNull(result.getName());
    }

    @Test
    void deleteOne_throwsException_whenTimeSlotNotFound() {
        //given
        when(timeSlotRepository.findById(ID)).thenReturn(Optional.empty());
        //when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.deleteOne(ID));
        //then
        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(MessageSource.TIME_SLOT_NOT_FOUND.getText(ID.toString()), exception.getMessage());
    }

    @Test
    void deleteOne_deletesTimeSlot() throws CustomException {
        //given
        TimeSlot entity = new TimeSlot();
        entity.setId(ID);
        entity.setName(STRING);
        when(timeSlotRepository.findById(ID)).thenReturn(Optional.of(entity));
        //when
        underTest.deleteOne(ID);
        //then
        verify(timeSlotRepository).delete(entity);
    }
}
