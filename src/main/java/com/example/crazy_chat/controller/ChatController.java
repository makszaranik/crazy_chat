package com.example.crazy_chat.controller;

import com.example.crazy_chat.domains.chat.ChatEntity;
import com.example.crazy_chat.domains.message.FileMessageEntity;
import com.example.crazy_chat.domains.message.TextMessageEntity;
import com.example.crazy_chat.dto.chat.ChatDto;
import com.example.crazy_chat.dto.chat.CreateChatDto;
import com.example.crazy_chat.dto.message.MessageResponse;
import com.example.crazy_chat.dto.message.input.TextMessageRequest;
import com.example.crazy_chat.dto.message.output.FileMessageResponse;
import com.example.crazy_chat.dto.message.output.TextMessageResponse;
import com.example.crazy_chat.dto.participant.input.ParticipantEventRequest;
import com.example.crazy_chat.dto.participant.output.ParticipantEventResponse;
import com.example.crazy_chat.service.ChatService;
import com.example.crazy_chat.service.EventService;
import com.example.crazy_chat.service.MessageService;
import com.example.crazy_chat.service.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.ArrayList;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final RabbitTemplate rabbitTemplate;
    private final MessageService messageService;
    private final ParticipantService participantService;


    @MutationMapping
    public ChatDto createChat(@Valid @Argument CreateChatDto chat) {

        ChatEntity chatEntity = ChatEntity.builder()
            .type(chat.type())
            .name(chat.name())
            .messageIds(new ArrayList<>())
            .participantIds(new ArrayList<>())
            .build();

        ChatEntity createdChat = chatService.createChat(chatEntity);
        log.info("chat created: {}", chat);

        return ChatDto.builder()
            .id(createdChat.getId())
            .name(createdChat.getName())
            .type(createdChat.getType())
            .messageIds(createdChat.getMessageIds())
            .participantIds(createdChat.getParticipantIds())
            .build();
    }


    @MutationMapping
    public TextMessageResponse sendTextMessage(@Valid @Argument TextMessageRequest message) {

        TextMessageEntity messageEntity = TextMessageEntity.builder()
            .chatId(message.chatId())
            .senderId(participantService.getCurrentParticipant().getId())
            .content(message.content())
            .build();

        TextMessageEntity savedMessage = messageService.saveMessage(messageEntity);
        chatService.addMessageToChat(message.chatId(), savedMessage);

        rabbitTemplate.convertAndSend(
            EventService.MESSAGE_EXCHANGE,
            EventService.MESSAGE_QUEUE,
            savedMessage
        );

        log.info("sent message: {}", savedMessage);

        return TextMessageResponse.builder()
            .id(savedMessage.getId())
            .chatId(savedMessage.getChatId())
            .senderId(savedMessage.getSenderId())
            .content(savedMessage.getContent())
            .build();
    }


    @MutationMapping
    public Boolean chatParticipantAction(@Valid @Argument ParticipantEventRequest chatEvent) {

        switch (chatEvent.event()) {
            case JOIN -> chatService.addParticipantToChat(chatEvent.chatId(), chatEvent.participantId());
            case LEAVE -> chatService.removeParticipantFromChat(chatEvent.chatId(), chatEvent.participantId());
        }

        rabbitTemplate.convertAndSend(
            EventService.PARTICIPANT_EVENT_EXCHANGE,
            EventService.PARTICIPANT_EVENT_QUEUE,
            chatEvent
        );

        return true;
    }


    @SubscriptionMapping
    public Flux<MessageResponse> messageSendEvent(@Argument String chatId) {
        return messageService.fetchEvents()
            .filter(event -> event.getChatId().equals(chatId))
            .map(event -> switch (event) {
                case TextMessageEntity message -> TextMessageResponse.builder()
                    .id(message.getId())
                    .chatId(message.getChatId())
                    .content(message.getContent())
                    .senderId(message.getSenderId())
                    .build();

                case FileMessageEntity message -> FileMessageResponse.builder()
                    .id(message.getId())
                    .chatId(message.getChatId())
                    .senderId(message.getSenderId())
                    .fileId(message.getS3FileId())
                    .build();
            });
    }


    @SubscriptionMapping
    public Flux<ParticipantEventResponse> chatParticipantEvent(@Argument String chatId) {
        return participantService.fetchEvents().filter(event -> event.chatId().equals(chatId));
    }

}
