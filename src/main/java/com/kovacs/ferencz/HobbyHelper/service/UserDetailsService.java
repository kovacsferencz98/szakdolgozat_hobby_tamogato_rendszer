package com.kovacs.ferencz.HobbyHelper.service;

import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.RegistrationVM;
import com.kovacs.ferencz.HobbyHelper.domain.Location;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.kovacs.ferencz.HobbyHelper.domain.UserDetails}.
 */
public interface UserDetailsService {

    /**
     * Save a userDetails.
     *
     * @param userDetailsDTO the entity to save.
     * @return the persisted entity.
     */
    UserDetailsDTO save(UserDetailsDTO userDetailsDTO);

    /**
     * Get all the userDetails.
     *
     * @return the list of entities.
     */
    List<UserDetailsDTO> findAll();

    /**
     * Get all the userDetails with the given residence
     *
     * @param residenceId the id of the given location
     * @return the list of entities.
     */
    List<UserDetailsDTO> findAllByResidence(Long residenceId);


    /**
     * Get the "id" userDetails.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserDetailsDTO> findOne(Long id);

    /**
     * Delete the "id" userDetails.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Delete the userDetails of the given user
     *
     * @param userId the id of the entity.
     */
    void deleteDetailOfUser(Long userId);

    /**
     * Saves the details of the new user.
     * @param registrationVM the details of the user to be registered.
     * @param  user the user entity the details belong to.
     * @return the entity.
     */
    public UserDetailsDTO registerUserDetails(RegistrationVM registrationVM, User user, Location location);

    /**
     * Get one userDetails by user id.
     *
     * @param userId the id of the entity.
     * @return the entity.
     */
    public Optional<UserDetailsDTO> findByUserId(Long userId);
}
