package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.domain.Event;
import com.kovacs.ferencz.HobbyHelper.domain.EventType;
import com.kovacs.ferencz.HobbyHelper.repository.EventTypeRepository;
import com.kovacs.ferencz.HobbyHelper.service.EventTypeService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventTypeDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EventTypeServiceImplTest {

    @MockBean
    private EventTypeRepository eventTypeRepository;

    @MockBean
    private EventTypeMapper eventTypeMapper;

    @Autowired
    EventTypeServiceImpl underTest;

    private EventType eventType;

    private EventTypeDTO eventTypeDTO;

    @BeforeEach
    void setUp() {
        eventType = initEventType();
        eventTypeDTO = initEventTypeDTO();
    }

    @Test
    void saveShouldSaveEntity() {
        //GIVEN
        given(eventTypeMapper.toEntity(any(EventTypeDTO.class))).willReturn(eventType);
        given(eventTypeMapper.toDto(any(EventType.class))).willReturn(eventTypeDTO);
        given(eventTypeRepository.save(any(EventType.class))).willReturn(eventType);
        //WHEN
        EventTypeDTO result = underTest.save(eventTypeDTO);
        //THEN
        BDDMockito.verify(eventTypeRepository).save(eventType);
        assertEquals(eventTypeDTO, result);
    }

    @Test
    void findAllShouldReturnAllEntities() {
        //GIVEN
        given(eventTypeRepository.findAll()).willReturn(Arrays.asList(eventType));
        given(eventTypeMapper.toDto(any(EventType.class))).willReturn(eventTypeDTO);
        //WHEN
        List<EventTypeDTO> result = underTest.findAll();
        //THEN
        verify(eventTypeRepository).findAll();
        assertEquals(Arrays.asList(eventTypeDTO), result);
    }

    @Test
    void findOneShouldReturnFoundEntity() {
        //GIVEN
        given(eventTypeRepository.findById(anyLong())).willReturn(Optional.of(eventType));
        given(eventTypeMapper.toDto(any(EventType.class))).willReturn(eventTypeDTO);
        //WHEN
        Optional<EventTypeDTO> result = underTest.findOne(1L);
        //THEN
        verify(eventTypeRepository).findById(1L);
        assertTrue(result.isPresent());
        assertEquals(eventTypeDTO, result.get());
    }

    @Test
    void findByName() {
        //GIVEN
        given(eventTypeRepository.findByName(anyString())).willReturn(Optional.of(eventType));
        given(eventTypeMapper.toDto(any(EventType.class))).willReturn(eventTypeDTO);
        //WHEN
        Optional<EventTypeDTO> result = underTest.findByName("Sport");
        //THEN
        verify(eventTypeRepository).findByName("Sport");
        assertTrue(result.isPresent());
        assertEquals(eventTypeDTO, result.get());
    }

    @Test
    void delete() {
        //GIVEN - in setup
        //WHEN
        underTest.delete(1L);
        //THEN
        verify(eventTypeRepository).deleteById(1L);
    }

    private EventTypeDTO initEventTypeDTO() {
        EventTypeDTO eventTypeDTO = new EventTypeDTO();
        eventTypeDTO.setBannerUrl("/");
        eventTypeDTO.setDescription("Description");
        eventTypeDTO.setId(1L);
        eventTypeDTO.setName("Sport");
        eventTypeDTO.setIconUrl("/");
        return eventTypeDTO;
    }

    private EventType initEventType() {
        EventType eventType = new EventType();
        eventType.setBannerUrl("/");
        eventType.setDescription("Description");
        eventType.setId(1L);
        eventType.setName("Sport");
        eventType.setIconUrl("/");
        return eventType;
    }
}