package com.example.crazy_chat.service;

import com.example.crazy_chat.domains.message.FileMessageEntity;
import com.example.crazy_chat.domains.message.MessageEntity;
import com.example.crazy_chat.domains.message.TextMessageEntity;
import com.example.crazy_chat.dto.message.TextMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    public static final String MESSAGE_QUEUE = "crazy_chat.message.queue";
    public static final String MESSAGE_EXCHANGE = "crazy_chat.message.exchange";

    private final MessagePublisherService messagePublisherService;

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(value = MESSAGE_QUEUE),
            exchange = @Exchange(value = MESSAGE_EXCHANGE),
            key = MESSAGE_QUEUE
        )
    )
    public void fetchMessages(MessageEntity message) {
        log.info("Fetching message: {}", message);
        messagePublisherService.publishMessage(message);
    }

}
