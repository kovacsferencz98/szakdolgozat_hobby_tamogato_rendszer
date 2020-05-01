package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.UnauthorizedRequest;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.security.SecurityUtils;
import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.LocationService;
import com.kovacs.ferencz.HobbyHelper.service.UserDetailsService;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
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
 * REST controller for managing {@link  com.kovacs.ferencz.HobbyHelper.domain.Location}.
 */
@RestController
@RequestMapping("/api")
public class LocationResource {

    public static class LocationResourceException extends RuntimeException {
        private LocationResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(LocationResource.class);

    private static final String ENTITY_NAME = "location";

    @Value("${application.name}")
    private String applicationName;

    private final LocationService locationService;

    private final UserDetailsService userDetailsService;

    private final EventService eventService;

    private final MessageSource messageSource;

    public LocationResource(LocationService locationService, MessageSource messageSource, UserDetailsService userDetailsService,
                            EventService eventService) {
        this.locationService = locationService;
        this.messageSource = messageSource;
        this.userDetailsService = userDetailsService;
        this.eventService = eventService;
    }

    /**
     * {@code POST  /locations} : Create a new location.
     *
     * @param locationDTO the locationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new locationDTO, or with status {@code 400 (Bad Request)} if the location has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/locations")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<LocationDTO> createLocation(@Valid @RequestBody LocationDTO locationDTO) throws URISyntaxException {
        log.debug("REST request to save Location : {}", locationDTO);
        if (locationDTO.getId() != null) {
            throw new LocationResourceException(messageSource.getMessage("location.idExists", null, LocaleUtil.getUserLocale()));
        }
        LocationDTO result = locationService.save(locationDTO);
        return ResponseEntity.created(new URI("/api/locations/" + result.getId())).body(result);
    }

    /**
     * {@code PUT  /locations} : Updates an existing location.
     *
     * @param locationDTO the locationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated locationDTO,
     * or with status {@code 400 (Bad Request)} if the locationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the locationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/locations")
    public ResponseEntity<LocationDTO> updateLocation(@Valid @RequestBody LocationDTO locationDTO) throws URISyntaxException {
        log.debug("REST request to update Location : {}", locationDTO);
        if(!isAuthorizedForModification(locationDTO.getId())) {
            throw new UnauthorizedRequest(messageSource.getMessage("unauthorized.request",
                    null, LocaleUtil.getUserLocale()));
        }
        if (locationDTO.getId() == null) {
            throw new LocationResourceException(messageSource.getMessage("location.missingId", null, LocaleUtil.getUserLocale()));
        }
        LocationDTO result = locationService.save(locationDTO);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code GET  /locations} : get all the locations.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of locations in body.
     */
    @GetMapping("/locations")
    public List<LocationDTO> getAllLocations() {
        log.debug("REST request to get all Locations");
        return locationService.findAll();
    }

    /**
     * {@code GET  /locations/:id} : get the "id" location.
     *
     * @param id the id of the locationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the locationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/locations/{id}")
    public ResponseEntity<LocationDTO> getLocation(@PathVariable Long id) {
        log.debug("REST request to get Location : {}", id);
        Optional<LocationDTO> locationDTO = locationService.findOne(id);
        return locationDTO.isPresent() ? ResponseEntity.ok().body(locationDTO.get()) : ResponseEntity.notFound().build();
    }

    /**
     * {@code DELETE  /locations/:id} : delete the "id" location.
     *
     * @param id the id of the locationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/locations/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        log.debug("REST request to delete Location : {}", id);
        if(isLocationUsed(id)) {
            throw new LocationResourceException(messageSource.getMessage("location.inUse", null, LocaleUtil.getUserLocale()));
        }
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isAuthorizedForModification(Long locationId) {
        String username = SecurityUtils.getCurrentUserLogin().get();
        boolean isAdmin = SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN);
        boolean isUserResidence = userDetailsService.findAllByResidence(locationId).stream()
                .filter(userDetailsDTO -> userDetailsDTO.getUserUsername().equals(username))
                .findFirst().isPresent();
        boolean isUserEventLocation = eventService.findAllByLocation(locationId).stream()
                .filter(eventDTO -> eventDTO.getCreatedByUsername().equals(username))
                .findFirst().isPresent();
        return isAdmin || isUserEventLocation || isUserResidence;
    }

    private boolean isLocationUsed(long locationId) {
        boolean usedAsUserResidence = !userDetailsService.findAllByResidence(locationId).isEmpty();
        boolean usedAsEventLocation = !eventService.findAllByLocation(locationId).isEmpty();
        return usedAsEventLocation || usedAsUserResidence;
    }
}
