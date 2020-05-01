package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.UnauthorizedRequest;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.CreateUserDetailVM;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.security.SecurityUtils;
import com.kovacs.ferencz.HobbyHelper.service.LocationService;
import com.kovacs.ferencz.HobbyHelper.service.UserDetailsService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;
import com.kovacs.ferencz.HobbyHelper.service.util.LocaleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link  com.kovacs.ferencz.HobbyHelper.domain.UserDetails}.
 */
@RestController
@RequestMapping("/api")
public class UserDetailsResource {

    public static class UserDetailsResourceException extends RuntimeException {
        private UserDetailsResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(UserDetailsResource.class);

    private static final String ENTITY_NAME = "userDetails";

    @Value("${application.name}")
    private String applicationName;

    private final UserDetailsService userDetailsService;

    private final UserService userService;

    private final LocationService locationService;

    private final MessageSource messageSource;

    public UserDetailsResource(UserDetailsService userDetailsService, LocationService locationService, MessageSource messageSource,
                               UserService userService) {
        this.userDetailsService = userDetailsService;
        this.locationService = locationService;
        this.messageSource = messageSource;
        this.userService = userService;
    }

    /**
     * {@code POST  /user-details} : Create a new userDetails.
     *
     * @param createUserDetailVM including data about the user detail entity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userDetailsDTO, or with status {@code 400 (Bad Request)} if the userDetails has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-details")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<UserDetailsDTO> createUserDetails(@Valid @RequestBody CreateUserDetailVM createUserDetailVM) throws URISyntaxException {
        log.debug("REST request to save UserDetails : {}", createUserDetailVM);
        if (createUserDetailVM.getUserDetails().getId() != null) {
            throw new UserDetailsResourceException(messageSource.getMessage("userDetail.idExists", null, LocaleUtil.getUserLocale()));
        }
        if (createUserDetailVM.getLocation().getId() != null) {
            throw new UserDetailsResourceException(messageSource.getMessage("location.idExists", null, LocaleUtil.getUserLocale()));
        }
        LocationDTO location = locationService.save(createUserDetailVM.getLocation());
        UserDetailsDTO userDetailsDTO = createUserDetailVM.getUserDetails();
        userDetailsDTO.setResidenceId(location.getId());
        UserDetailsDTO result = userDetailsService.save(userDetailsDTO);
        return ResponseEntity.created(new URI("/api/user-details/" + result.getId()))
                .body(result);
    }

    /**
     * {@code PUT  /user-details} : Updates an existing userDetails.
     *
     * @param userDetailsDTO the userDetailsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userDetailsDTO,
     * or with status {@code 400 (Bad Request)} if the userDetailsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userDetailsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-details")
    public ResponseEntity<UserDetailsDTO> updateUserDetails(@Valid @RequestBody UserDetailsDTO userDetailsDTO) throws URISyntaxException {
        log.debug("REST request to update UserDetails : {}", userDetailsDTO);
        if(!isAuthorizedForModification(userDetailsDTO)) {
            throw new UnauthorizedRequest(messageSource.getMessage("unauthorized.request",
                    null, LocaleUtil.getUserLocale()));
        }
        if (userDetailsDTO.getId() == null) {
            throw new UserDetailsResourceException(messageSource.getMessage("userDetail.missingId", null, LocaleUtil.getUserLocale()));
        }
        UserDetailsDTO result = userDetailsService.save(userDetailsDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    /**
     * {@code GET  /user-details} : get all the userDetails.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userDetails in body.
     */
    @GetMapping("/user-details")
    public List<UserDetailsDTO> getAllUserDetails() {
        log.debug("REST request to get all UserDetails");
        return userDetailsService.findAll();
    }

    /**
     * {@code GET  /user-details/:id} : get the "id" userDetails.
     *
     * @param id the id of the userDetailsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userDetailsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-details/{id}")
    public ResponseEntity<UserDetailsDTO> getUserDetails(@PathVariable Long id) {
        log.debug("REST request to get UserDetails : {}", id);
        Optional<UserDetailsDTO> userDetailsDTO = userDetailsService.findOne(id);
        return userDetailsDTO.isPresent() ? ResponseEntity.ok().body(userDetailsDTO.get()) : ResponseEntity.notFound().build();
    }

    /**
     * {@code DELETE  /user-details/:id} : delete the "id" userDetails.
     *
     * @param id the id of the userDetailsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-details/{id}")
    public ResponseEntity<Void> deleteUserDetails(@PathVariable Long id) {
        log.debug("REST request to delete UserDetails : {}", id);
        UserDetailsDTO userDetailsDTO = findUserDetails(id);
        if(!isAuthorizedForModification(userDetailsDTO)) {
            throw new UnauthorizedRequest(messageSource.getMessage("unauthorized.request",
                    null, LocaleUtil.getUserLocale()));
        }
        if(isUserDetailInUse(userDetailsService.findOne(id).get())) {
            throw new UserDetailsResourceException(messageSource.getMessage("userDetail.inUse", null, LocaleUtil.getUserLocale()));
        }
        userDetailsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isUserDetailInUse(UserDetailsDTO userDetailsDTO) {
        Optional<User> user = userService.getUserWithAuthorities(userDetailsDTO.getUserId());
        boolean inUse = false;
        if(user.isPresent()) {
            inUse = true;
        }
        return inUse;
    }

    private UserDetailsDTO findUserDetails(Long id) {
        Optional<UserDetailsDTO> userDetails = userDetailsService.findOne(id);
        if(userDetails.isEmpty()) {
            throw new UserDetailsResourceException(messageSource.getMessage("userDetail.notFound", null, LocaleUtil.getUserLocale()));
        }
        return userDetails.get();
    }

    private boolean isAuthorizedForModification(UserDetailsDTO userDetailsDTO) {
        return SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)
                || userDetailsDTO.getUserUsername().equals(SecurityUtils.getCurrentUserLogin().get());
    }
}
