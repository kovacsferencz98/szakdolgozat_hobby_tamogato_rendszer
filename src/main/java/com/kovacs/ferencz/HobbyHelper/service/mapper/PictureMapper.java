package com.kovacs.ferencz.HobbyHelper.service.mapper;

import com.kovacs.ferencz.HobbyHelper.domain.Picture;
import com.kovacs.ferencz.HobbyHelper.service.dto.PictureDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link com.kovacs.ferencz.HobbyHelper.domain.Picture} and its DTO {@link {PictureDTO}}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PictureMapper extends EntityMapper<PictureDTO, Picture> {

    default Picture fromId(Long id) {
        if (id == null) {
            return null;
        }
        Picture picture = new Picture();
        picture.setId(id);
        return picture;
    }
}
