package com.example.crazy_chat.service;

import com.example.crazy_chat.domains.chat.ChatEntity;
import com.example.crazy_chat.domains.message.MessageEntity;
import com.example.crazy_chat.exceptions.ChatNotFoundException;
import com.example.crazy_chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatEntity createChat(ChatEntity chatEntity) {
        return chatRepository.save(chatEntity);
    }

    public void addMessageToChat(String chatId, MessageEntity message) {
        ChatEntity chat = findChatById(chatId).orElseThrow(() -> new ChatNotFoundException(message.getChatId()));
        chat.getMessageIds().add(message.getId());
        chatRepository.save(chat);
    }

    public Optional<ChatEntity> findChatById(String id) {
        return chatRepository.findById(id);
    }

}
