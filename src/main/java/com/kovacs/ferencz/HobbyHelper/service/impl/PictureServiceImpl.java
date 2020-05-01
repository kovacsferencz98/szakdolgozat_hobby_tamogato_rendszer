package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.domain.Picture;
import com.kovacs.ferencz.HobbyHelper.repository.PictureRepository;
import com.kovacs.ferencz.HobbyHelper.service.PictureService;
import com.kovacs.ferencz.HobbyHelper.service.dto.PictureDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.PictureMapper;
import com.kovacs.ferencz.HobbyHelper.service.util.LocaleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Picture}.
 */
@Service
public class PictureServiceImpl implements PictureService {

    public static class PictureServiceException extends RuntimeException {
        private PictureServiceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(PictureServiceImpl.class);

    private PictureRepository pictureRepository;

    private PictureMapper pictureMapper;

    private MessageSource messageSource;

    public PictureServiceImpl(PictureRepository pictureRepository, PictureMapper pictureMapper, MessageSource messageSource) {
        this.pictureRepository = pictureRepository;
        this.pictureMapper = pictureMapper;
        this.messageSource = messageSource;
    }

    /**
     * Saves a Picture
     * @param file the file to save
     * @return the persisted entity.
     */
    @Override
    public PictureDTO storeFile(MultipartFile file) {
        log.debug("Request to save Picture : {}", file.getName());
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            log.debug("File name: " + fileName);
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new PictureServiceException(messageSource.getMessage("picture.invalidName", null, LocaleUtil.getUserLocale()));
            }

            Picture picture = new Picture(fileName, file.getContentType(), file.getBytes());
            Picture savedPicture = pictureRepository.save(picture);
            return pictureMapper.toDto(savedPicture);
        } catch (IOException ex) {
            throw new PictureServiceException(messageSource.getMessage("picture.notStored", null, LocaleUtil.getUserLocale()));
        }
    }

    /**
     * Get Picture with id
     * @param fileId the id of the Picture entity
     * @return the entity
     */
    @Override
    public Optional<PictureDTO> getFile(Long fileId) {
        log.debug("Request to get Picture : {}", fileId);
        return pictureRepository.findById(fileId).map(pictureMapper::toDto);
    }
}
