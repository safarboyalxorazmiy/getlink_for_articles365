package com.alcode.sentHistory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SentHistoryService {
    private final SentHistoryRepository sentHistoryRepository;

    public Boolean create (Long chatId) {
        if (isSent(chatId)) {
            return false;
        }

        SentHistoryEntity sentHistory = new SentHistoryEntity();
        sentHistory.setChatId(chatId);
        sentHistoryRepository.save(sentHistory);

        return true;
    }

    public Boolean isSent(Long chatId) {
        return sentHistoryRepository.findByChatId(chatId).isPresent();
    }
}