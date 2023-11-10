package com.alcode.sentHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SentHistoryRepository extends JpaRepository<SentHistoryEntity, Long> {
    Optional<SentHistoryEntity> findByChatId(Long chatId);
}