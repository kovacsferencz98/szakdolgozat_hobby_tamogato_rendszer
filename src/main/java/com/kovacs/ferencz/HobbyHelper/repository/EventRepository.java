package com.kovacs.ferencz.HobbyHelper.repository;

import com.kovacs.ferencz.HobbyHelper.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Event entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByCreatedBy_Id(Long id);
    List<Event> findAllByLocation_Id(Long id);
    List<Event> findAllByType_Id(Long id);
}
