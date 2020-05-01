package com.kovacs.ferencz.HobbyHelper.repository;

import com.kovacs.ferencz.HobbyHelper.domain.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Picture entity.
 */
@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {

}
