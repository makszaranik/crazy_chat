package com.example.crazy_chat.service.chat;

import com.example.crazy_chat.domains.chat.ChatEntity;
import com.example.crazy_chat.domains.message.MessageEntity;
import com.example.crazy_chat.domains.participant.ParticipantEntity;
import com.example.crazy_chat.dto.chat.ChatResponse;
import com.example.crazy_chat.exceptions.ChatNotFoundException;
import com.example.crazy_chat.repository.ChatRepository;
import com.example.crazy_chat.repository.MessageRepository;
import com.example.crazy_chat.service.participant.ParticipantService;
import com.example.crazy_chat.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ParticipantService participantService;
    private final MessageService messageService;

    public ChatEntity createChat(ChatEntity chatEntity) {
        return chatRepository.save(chatEntity);
    }

    public ChatEntity findChatById(String id) {
        return chatRepository.findById(id)
            .orElseThrow(() -> new ChatNotFoundException(id));
    }

    public void addMessageToChat(String chatId, MessageEntity message) {
        String participantId = participantService.getCurrentParticipant().getId();
        message.setChatId(chatId);
        message.setSenderId(participantId);
        messageRepository.save(message);
    }

    public List<MessageEntity> getMessages(String chatId) {
        return messageRepository.findAllByChatId(chatId);
    }

    public void addParticipantToChat(String chatId, String participantId) {
        ChatEntity chat = findChatById(chatId);
        boolean exists = chat.getParticipants().stream().anyMatch(p -> p.getId().equals(participantId));
        if (!exists) {
            ParticipantEntity participant = participantService.fetchParticipantById(participantId);
            chat.getParticipants().add(participant);
            chatRepository.save(chat);
        }
    }

    public void removeParticipantFromChat(String chatId, String participantId) {
        ChatEntity chat = findChatById(chatId);
        chat.getParticipants().removeIf(p -> p.getId().equals(participantId));
        chatRepository.save(chat);
    }

    public boolean isParticipantInChat(String chatId, String participantId) {
        ChatEntity chat = findChatById(chatId);
        return chat.getParticipants().stream().anyMatch(p -> p.getId().equals(participantId));
    }

    public List<ChatEntity> fetchAllChats() {
        return chatRepository.findAll();
    }

}