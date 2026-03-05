package com.example.crazy_chat.service;

import com.example.crazy_chat.domains.message.FileMessageEntity;
import com.example.crazy_chat.domains.message.MessageEntity;
import com.example.crazy_chat.domains.message.TextMessageEntity;
import com.example.crazy_chat.domains.eventOutbox.EventOutBoxEntity;
import com.example.crazy_chat.dto.message.output.MessageResponse;
import com.example.crazy_chat.dto.message.output.FileMessageResponse;
import com.example.crazy_chat.dto.message.output.TextMessageResponse;
import com.example.crazy_chat.repository.MessageRepository;
import com.example.crazy_chat.repository.EventOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final EventOutboxRepository eventOutboxRepository;
    private final Sinks.Many<MessageEntity> messageBuffer = Sinks.many().multicast().directBestEffort();

    public MessageEntity saveMessage(MessageEntity messageEntity) {
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

    public MessageEntity fetchMessageById(String id){
        return messageRepository.findById(id).orElseThrow(() -> new IllegalStateException("message not exist"));
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

    @Transactional
    public MessageEntity sendMessageWithOutbox(MessageEntity messageEntity) {
        MessageEntity savedMessage = saveMessage(messageEntity);

        EventOutBoxEntity outBoxEvent = EventOutBoxEntity.builder()
            .messageId(messageEntity.getId())
            .status(EventOutBoxEntity.AggregateType.CREATED)
            .build();

        eventOutboxRepository.save(outBoxEvent);
        return savedMessage;
    }


    public List<EventOutBoxEntity> getAllWithStatusCreated() {
        return eventOutboxRepository.findOutBoxEventEntitiesByStatus(EventOutBoxEntity.AggregateType.CREATED);
    }
}
