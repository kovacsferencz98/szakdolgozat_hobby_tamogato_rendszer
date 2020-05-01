package com.kovacs.ferencz.HobbyHelper.service;


import com.kovacs.ferencz.HobbyHelper.service.dto.RoleDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.kovacs.ferencz.HobbyHelper.domain.Role}.
 */
public interface RoleService {

    /**
     * Save a role.
     *
     * @param roleDTO the entity to save.
     * @return the persisted entity.
     */
    RoleDTO save(RoleDTO roleDTO);

    /**
     * Get all the roles.
     *
     * @return the list of entities.
     */
    List<RoleDTO> findAll();


    /**
     * Get the "id" role.
     *
     * @param name the id of the entity.
     * @return the entity.
     */
    Optional<RoleDTO> findOne(String name);

    /**
     * Delete the "id" role.
     *
     * @param name the id of the entity.
     */
    void delete(String name);
}
