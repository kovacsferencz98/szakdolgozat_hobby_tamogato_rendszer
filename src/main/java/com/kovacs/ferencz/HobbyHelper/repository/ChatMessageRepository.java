package com.kovacs.ferencz.HobbyHelper.repository;

import com.kovacs.ferencz.HobbyHelper.domain.ChatMessage;
import com.kovacs.ferencz.HobbyHelper.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the ChatMessage entity.
 */
@Repository
public interface ChatMessageRepository  extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByEvent_Id(Long id);
}
