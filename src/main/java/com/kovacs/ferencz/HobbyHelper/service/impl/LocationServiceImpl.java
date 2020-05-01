package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.RegistrationVM;
import com.kovacs.ferencz.HobbyHelper.domain.Location;
import com.kovacs.ferencz.HobbyHelper.repository.LocationRepository;
import com.kovacs.ferencz.HobbyHelper.service.LocationService;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.LocationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Location}.
 */
@Service
@Transactional
public class LocationServiceImpl implements LocationService {

    private final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);

    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;

    public LocationServiceImpl(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    /**
     * Save a location.
     *
     * @param locationDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public LocationDTO save(LocationDTO locationDTO) {
        log.debug("Request to save Location : {}", locationDTO);
        Location location = locationMapper.toEntity(locationDTO);
        location = locationRepository.save(location);
        return locationMapper.toDto(location);
    }

    /**
     * Get all the locations.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LocationDTO> findAll() {
        log.debug("Request to get all Locations");
        return locationRepository.findAll().stream()
            .map(locationMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one location by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<LocationDTO> findOne(Long id) {
        log.debug("Request to get Location : {}", id);
        return locationRepository.findById(id)
            .map(locationMapper::toDto);
    }

    /**
     * Delete the location by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Location : {}", id);
        locationRepository.deleteById(id);
    }

    @Override
    public Location registerLocation(RegistrationVM registrationVM) {
        LocationDTO locationDTO = convertRegistrationVMToLocationDTO(registrationVM);
        log.debug("Request to save Location : {}", locationDTO);
        Location location = locationMapper.toEntity(locationDTO);
        return locationRepository.save(location);
    }

    private LocationDTO convertRegistrationVMToLocationDTO(RegistrationVM registrationVM) {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setCountry(registrationVM.getCountry());
        locationDTO.setCity(registrationVM.getCity());
        locationDTO.setRegion(registrationVM.getRegion());
        locationDTO.setZip(registrationVM.getZip());
        locationDTO.setStreet(registrationVM.getStreet());
        locationDTO.setNumber(registrationVM.getNumber());
        locationDTO.setApartment(registrationVM.getApartment());
        locationDTO.setLatitude(registrationVM.getLatitude());
        locationDTO.setLongitude(registrationVM.getLongitude());
        return locationDTO;
    }
}
