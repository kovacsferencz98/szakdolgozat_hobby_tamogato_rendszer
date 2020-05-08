package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.RegistrationVM;
import com.kovacs.ferencz.HobbyHelper.domain.Location;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.domain.UserDetails;
import com.kovacs.ferencz.HobbyHelper.repository.UserDetailsRepository;
import com.kovacs.ferencz.HobbyHelper.service.LocationService;
import com.kovacs.ferencz.HobbyHelper.service.PictureService;
import com.kovacs.ferencz.HobbyHelper.service.UserDetailsService;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.UserDetailsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link UserDetails}.
 */
@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserDetailsRepository userDetailsRepository;

    private final UserDetailsMapper userDetailsMapper;

    private final LocationService locationService;

    private final PictureService pictureService;

    public UserDetailsServiceImpl(UserDetailsRepository userDetailsRepository, UserDetailsMapper userDetailsMapper,
                                  LocationService locationService, PictureService pictureService) {
        this.userDetailsRepository = userDetailsRepository;
        this.userDetailsMapper = userDetailsMapper;
        this.locationService = locationService;
        this.pictureService = pictureService;
    }

    /**
     * Save a userDetails.
     *
     * @param userDetailsDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public UserDetailsDTO save(UserDetailsDTO userDetailsDTO) {
        log.debug("Request to save UserDetails : {}", userDetailsDTO);
        UserDetails userDetails = userDetailsMapper.toEntity(userDetailsDTO);
        userDetails = userDetailsRepository.save(userDetails);
        return userDetailsMapper.toDto(userDetails);
    }

    /**
     * Get all the userDetails.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDetailsDTO> findAll() {
        log.debug("Request to get all UserDetails");
        return userDetailsRepository.findAll().stream()
            .map(userDetailsMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the userDetails with the given residence
     *
     * @param residenceId the id of the given location
     * @return the list of entities.
     */
    public List<UserDetailsDTO> findAllByResidence(Long residenceId) {
        log.debug("Request to get all UserDetails for location: " + residenceId);
        return userDetailsRepository.findAllByResidence_Id(residenceId).stream()
                .map(userDetailsMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one userDetails by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserDetailsDTO> findOne(Long id) {
        log.debug("Request to get UserDetails : {}", id);
        return userDetailsRepository.findById(id)
            .map(userDetailsMapper::toDto);
    }

    /**
     * Get one userDetails by user id.
     *
     * @param userId the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserDetailsDTO> findByUserId(Long userId) {
        log.debug("Request to get UserDetails by userId: {}", userId);
        return userDetailsRepository.findOneByUser_Id(userId)
                .map(userDetailsMapper::toDto);
    }

    /**
     * Delete the userDetails by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserDetails : {}", id);
        Optional<UserDetailsDTO> userDetails = findOne(id);
        userDetailsRepository.deleteById(id);
        userDetails.ifPresent(userDetailsDTO -> {
            locationService.delete(userDetailsDTO.getResidenceId());
            if(userDetailsDTO.getProfilePicId() != null) {
                pictureService.deleteFile(userDetailsDTO.getProfilePicId());
            }
        });
    }

    /**
     * Delete the userDetails of the given user
     *
     * @param userId the id of the entity.
     */
    @Override
    public void deleteDetailOfUser(Long userId) {
        userDetailsRepository.findOneByUser_Id(userId)
                .ifPresent(userDetails -> delete(userDetails.getId()));
    }

    /**
     * Saves the details of the new user.
     * @param registrationVM the details of the user to be registered.
     * @return the entity.
     */
    @Override
    public UserDetailsDTO registerUserDetails(RegistrationVM registrationVM, User user, Location location) {
        UserDetailsDTO userDetailsDTO = convertRegistrationVMToUserDetailsDTO(registrationVM);
        userDetailsDTO.setUserId(user.getId());
        userDetailsDTO.setUserUsername(user.getUsername());
        userDetailsDTO.setResidenceId(location.getId());
        return save(userDetailsDTO);
    }

    private UserDetailsDTO convertRegistrationVMToUserDetailsDTO(RegistrationVM registrationVM) {
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setDescription(registrationVM.getDescription());
        userDetailsDTO.setProfilePicId(registrationVM.getProfilePicId());
        return userDetailsDTO;
    }
}
