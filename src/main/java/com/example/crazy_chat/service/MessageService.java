package com.example.crazy_chat.service;

import com.example.crazy_chat.domains.message.FileMessageEntity;
import com.example.crazy_chat.domains.message.MessageEntity;
import com.example.crazy_chat.domains.message.TextMessageEntity;
import com.example.crazy_chat.dto.message.output.MessageResponse;
import com.example.crazy_chat.dto.message.output.FileMessageResponse;
import com.example.crazy_chat.dto.message.output.TextMessageResponse;
import com.example.crazy_chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final Sinks.Many<MessageEntity> messageBuffer = Sinks.many().multicast().directBestEffort();

    public TextMessageEntity saveMessage(TextMessageEntity messageEntity) {
        return messageRepository.save(messageEntity);
    }

    public FileMessageEntity saveMessage(FileMessageEntity messageEntity) {
        return messageRepository.save(messageEntity);
    }

    public Flux<MessageEntity> fetchEvents() {
        return messageBuffer.asFlux();
    }

    public void publishMessageEvent(MessageEntity message) {
        messageBuffer.tryEmitNext(message);
    }

    public MessageResponse toMessageResponse(MessageEntity message) {
        return switch (message) {
            case TextMessageEntity textMessage -> TextMessageResponse.builder()
                .id(textMessage.getId())
                .chatId(textMessage.getChatId())
                .senderId(textMessage.getSenderId())
                .content(textMessage.getContent())
                .build();

            case FileMessageEntity textMessage -> FileMessageResponse.builder()
                .id(textMessage.getId())
                .chatId(textMessage.getChatId())
                .senderId(textMessage.getSenderId())
                .fileId(textMessage.getS3FileId())
                .build();
        };
    }

    public List<MessageResponse> toMessageResponse(List<MessageEntity> messages) {
        return messages.stream().map(this::toMessageResponse).toList();
    }

    public List<MessageEntity> fetchMessages(String chatId) {
        return messageRepository.findAllByChatId(chatId);
    }

    public List<MessageResponse> fetchMessagesToMessageResponse(String chatId) {
        return fetchMessages(chatId).stream()
            .map(this::toMessageResponse)
            .toList();
    }

    public Map<String, List<MessageResponse>> fetchMessagesByChatIds(List<String> chatIds) {
        List<MessageEntity> messages = messageRepository.findByChatIdIn(chatIds);
        return messages.stream().collect(
            Collectors.groupingBy(
                MessageEntity::getChatId,
                Collectors.mapping(this::toMessageResponse, Collectors.toList())
            )
        );
    }
}
