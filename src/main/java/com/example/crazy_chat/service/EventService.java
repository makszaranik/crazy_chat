package com.example.crazy_chat.service;

import com.example.crazy_chat.domains.message.MessageEntity;
import com.example.crazy_chat.dto.participant.output.ParticipantChatEventResponse;
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

    public static final String PARTICIPANT_EVENT_QUEUE = "crazy_chat.participant.event.queue";
    public static final String PARTICIPANT_EVENT_EXCHANGE = "crazy_chat.participant.event.exchange";

    private final MessageService messageService;
    private final ParticipantService participantService;


    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(value = MESSAGE_QUEUE),
            exchange = @Exchange(value = MESSAGE_EXCHANGE),
            key = MESSAGE_QUEUE
        )
    )
    public void fetchMessageEvents(MessageEntity message) {
        log.debug("Fetching message: {}", message);
        messageService.publishMessageEvent(message);
    }


    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(value = PARTICIPANT_EVENT_QUEUE),
            exchange = @Exchange(value = PARTICIPANT_EVENT_EXCHANGE),
            key = PARTICIPANT_EVENT_QUEUE
        )
    )
    public void fetchParticipantEvents(ParticipantChatEventResponse event) {
        log.debug("Fetching participant event: {}", event);
        participantService.publishEvent(event);
    }


}
