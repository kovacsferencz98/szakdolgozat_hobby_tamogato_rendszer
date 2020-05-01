package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.domain.Event;
import com.kovacs.ferencz.HobbyHelper.domain.EventType;
import com.kovacs.ferencz.HobbyHelper.repository.EventTypeRepository;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventTypeDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventMapper;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EventTypeServiceIT {

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @Autowired
    EventTypeMapper eventTypeMapper;

    @Autowired
    private EventTypeServiceImpl underTest;

    private EventType eventType;

    @BeforeEach
    void setUp() {
        clearDatabase();
        eventType = initEventType();
    }

    @Test
    @Transactional
    void saveShouldCreateDatabaseEntity() {
        //GIVEN
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);
        //WHEN
        EventTypeDTO response = underTest.save(eventTypeDTO);
        //THEN
        Optional<EventType> saved = eventTypeRepository.findById(response.getId());
        assertTrue(saved.isPresent());
        EventType eventTypeObtained = saved.get();
        eventTypeObtained.setId(null);
        eventType.setId(null);
        assertEquals(eventType, eventTypeObtained);
    }

    @Test
    @Transactional
    void findAllShouldReturnEntities() {
        //GIVEN
        EventType saved = eventTypeRepository.saveAndFlush(eventType);
        //WHEN
        List<EventTypeDTO> result = underTest.findAll();
        //THEN
        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(eventTypeMapper.toDto(saved)), result);
    }

    @Test
    @Transactional
    void findOneShouldReturnEntity() {
        //GIVEN
        EventType saved = eventTypeRepository.saveAndFlush(eventType);
        //WHEN
        Optional<EventTypeDTO> result = underTest.findOne(saved.getId());
        //THEN
        assertTrue(result.isPresent());
        assertEquals(eventTypeMapper.toDto(saved), result.get());
    }

    @Test
    @Transactional
    void findByNameShouldReturnEntity() {
        //GIVEN
        String name = eventType.getName();
        EventType saved = eventTypeRepository.saveAndFlush(eventType);
        //WHEN
        Optional<EventTypeDTO> result = underTest.findByName(name);
        //THEN
        assertTrue(result.isPresent());
        assertEquals(eventTypeMapper.toDto(saved), result.get());
    }

    @Test
    @Transactional
    void deleteShouldDeleteEntity() {
        //GIVEN
        EventType saved = eventTypeRepository.saveAndFlush(eventType);
        //WHEN
        underTest.delete(saved.getId());
        //THEN
        Optional<EventType> entity = eventTypeRepository.findById(saved.getId());
        assertFalse(entity.isPresent());
    }


    private void clearDatabase() {
        eventTypeRepository.deleteAll();
        eventTypeRepository.flush();
    }

    private EventType initEventType() {
        EventType type = new EventType();
        type.setId(1L);
        type.setDescription("Sport");
        type.setName("Sport");
        type.setBannerUrl("/");
        type.setIconUrl("/");
        return type;
    }

}
