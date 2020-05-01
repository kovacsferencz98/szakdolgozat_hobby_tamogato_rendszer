package com.kovacs.ferencz.HobbyHelper.service.mapper;

import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.service.dto.RoleDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Role} and its DTO {@link RoleDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface RoleMapper extends EntityMapper<RoleDTO, Role> {

    default Role fromId(String name) {
        if (name == null) {
            return null;
        }
        Role role = new Role();
        role.setName(name);
        return role;
    }
}
