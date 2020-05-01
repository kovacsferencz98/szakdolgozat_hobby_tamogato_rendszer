package com.kovacs.ferencz.HobbyHelper.service.mapper;

import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventTypeDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link EventType} and its DTO {@link EventTypeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EventTypeMapper extends EntityMapper<EventTypeDTO, EventType> {



    default EventType fromId(Long id) {
        if (id == null) {
            return null;
        }
        EventType eventType = new EventType();
        eventType.setId(id);
        return eventType;
    }
}
