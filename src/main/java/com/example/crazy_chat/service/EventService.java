package com.example.crazy_chat.service;

import com.example.crazy_chat.domains.message.MessageEntity;
import com.example.crazy_chat.domains.message.outboxEvent.OutBoxEventEntity;
import com.example.crazy_chat.dto.participant.output.ParticipantChatEventResponse;
import com.example.crazy_chat.repository.OutBoxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private final RabbitTemplate rabbitTemplate;
    private final OutBoxEventRepository eventRepository;

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

    @Scheduled(fixedRate = 2000)
    public void publishEvent() {
        List<OutBoxEventEntity> withStatusCreated = messageService.getAllWithStatusCreated();
        for (OutBoxEventEntity event : withStatusCreated) {
            try {
                MessageEntity messageEntity = messageService.fetchMessageById(event.getId());

                rabbitTemplate.convertAndSend(
                    EventService.MESSAGE_EXCHANGE,
                    EventService.MESSAGE_QUEUE,
                    messageEntity
                );

                event.setStatus(OutBoxEventEntity.AggregateType.DELIVERED);
                eventRepository.save(event);

            } catch (Exception exception) {
                log.error("outbox publishing error");
            }
        }
    }

}
