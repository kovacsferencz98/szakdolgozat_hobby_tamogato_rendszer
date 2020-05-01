package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.SavedPictureVM;
import com.kovacs.ferencz.HobbyHelper.service.PictureService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.PictureDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PictureController {

    private static final Logger logger = LoggerFactory.getLogger(PictureController.class);

    private final PictureService pictureService;

    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    /**
     * {@code GET profile/:id} : Get Profile Details
     *
     * @param file the file to be saved.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the PictureDTO representing the saved picture
     *
     * @throws URISyntaxException
     */
    @PostMapping("/uploadPicture")
    public ResponseEntity<SavedPictureVM> uploadPicture(@RequestParam("file") MultipartFile file) throws URISyntaxException {
        PictureDTO pictureDTO = pictureService.storeFile(file);
        return ResponseEntity.created(new URI("/api/downloadPicture/" + pictureDTO.getId()))
                .body(SavedPictureVM.fromPictureDTO(pictureDTO));
    }

    @GetMapping("/downloadPicture/{fileId}")
    public ResponseEntity<Resource> downloadPicture(@PathVariable Long fileId) {
        Optional<PictureDTO> pictureDTO = pictureService.getFile(fileId);
        return pictureDTO.isPresent() ? ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(pictureDTO.get().getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pictureDTO.get().getFileName() + "\"")
                .body(new ByteArrayResource(pictureDTO.get().getData())) : ResponseEntity.notFound().build();
    }
}
