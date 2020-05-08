package com.kovacs.ferencz.HobbyHelper.service;

import com.kovacs.ferencz.HobbyHelper.service.dto.PictureDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.kovacs.ferencz.HobbyHelper.domain.Picture}.
 */
public interface PictureService {
    /**
     * Saves a Picture
     * @param file the file to save
     * @return the persisted entity.
     */
    PictureDTO storeFile(MultipartFile file);

    /**
     * Get Picture with id
     * @param fileId the id of the Picture entity
     * @return the entity
     */
    Optional<PictureDTO> getFile(Long fileId);

    /**
     * Deletes Picture with id
     * @param fileId the id of the Picture entity
     */
    void deleteFile(Long fileId);


}
