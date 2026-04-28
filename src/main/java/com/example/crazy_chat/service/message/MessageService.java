package com.example.crazy_chat.service.message;

import com.example.crazy_chat.domains.message.MessageEntity;
import com.example.crazy_chat.domains.eventOutbox.EventOutBoxEntity;
import com.example.crazy_chat.dto.message.output.MessageResponse;
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
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final EventOutboxRepository eventOutboxRepository;
    private final Sinks.Many<MessageEntity> messageBuffer = Sinks.many().multicast().directBestEffort();
    private final MessageMapperService messageMapperService;

    public <T extends MessageEntity> T saveMessage(T messageEntity) {
        return messageRepository.save(messageEntity);
    }

    public Flux<MessageEntity> fetchEvents() {
        return messageBuffer.asFlux();
    }

    public void publishMessageEvent(MessageEntity message) {
        messageBuffer.tryEmitNext(message);
    }


    public MessageEntity fetchMessageById(String id) {
        return messageRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
    }

    public List<MessageResponse> fetchMessages(String chatId) {
        return messageRepository.findAllByChatId(chatId).stream()
            .map(messageMapperService::toMessageResponse)
            .toList();
    }

    public Map<String, List<MessageResponse>> fetchMessagesByChatIds(List<String> chatIds) {
        List<MessageEntity> messages = messageRepository.findByChatIdIn(chatIds);
        return messages.stream().collect(
            Collectors.groupingBy(
                MessageEntity::getChatId,
                Collectors.mapping(messageMapperService::toMessageResponse, Collectors.toList())
            )
        );
    }

    @Transactional
    public <T extends MessageEntity> T sendMessageWithOutbox(T messageEntity) {
        T savedMessage = saveMessage(messageEntity);

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
