package com.kovacs.ferencz.HobbyHelper.service.mapper;

import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link EventParticipant} and its DTO {@link EventParticipantDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, EventMapper.class})
public interface EventParticipantMapper extends EntityMapper<EventParticipantDTO, EventParticipant> {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "userUsername")
    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "event.name", target = "eventName")
    EventParticipantDTO toDto(EventParticipant eventParticipant);

    @Mapping(source = "userId", target = "user")
    @Mapping(source = "eventId", target = "event")
    EventParticipant toEntity(EventParticipantDTO eventParticipantDTO);

    default EventParticipant fromId(Long id) {
        if (id == null) {
            return null;
        }
        EventParticipant eventParticipant = new EventParticipant();
        eventParticipant.setId(id);
        return eventParticipant;
    }
}
