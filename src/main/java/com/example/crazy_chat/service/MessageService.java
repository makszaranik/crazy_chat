package com.example.crazy_chat.service;

import com.example.crazy_chat.domains.message.MessageEntity;
import com.example.crazy_chat.domains.message.TextMessageEntity;
import com.example.crazy_chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final Sinks.Many<MessageEntity> messageBuffer = Sinks.many().multicast().directBestEffort();

    public TextMessageEntity saveMessage(TextMessageEntity messageEntity) {
        return messageRepository.save(messageEntity);
    }

    public Flux<MessageEntity> fetchEvents() {
        return messageBuffer.asFlux();
    }

    public void publishMessageEvent(MessageEntity message) {
        messageBuffer.tryEmitNext(message);
    }

}
