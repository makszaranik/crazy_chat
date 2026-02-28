package com.example.crazy_chat.controller;

import com.example.crazy_chat.domains.chat.ChatEntity;
import com.example.crazy_chat.domains.message.FileMessageEntity;
import com.example.crazy_chat.domains.message.MessageEntity;
import com.example.crazy_chat.domains.message.TextMessageEntity;
import com.example.crazy_chat.domains.participant.ParticipantEntity;
import com.example.crazy_chat.dto.chat.ChatResponse;
import com.example.crazy_chat.dto.chat.CreateChatRequest;
import com.example.crazy_chat.dto.message.MessageResponse;
import com.example.crazy_chat.dto.message.input.TextMessageRequest;
import com.example.crazy_chat.dto.message.output.FileMessageResponse;
import com.example.crazy_chat.dto.message.output.TextMessageResponse;
import com.example.crazy_chat.dto.participant.input.ParticipantEventRequest;
import com.example.crazy_chat.dto.participant.output.ParticipantChatEventResponse;
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
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final RabbitTemplate rabbitTemplate;
    private final MessageService messageService;
    private final ParticipantService participantService;


    @QueryMapping
    public ChatResponse chat(@Argument String id) {

        ChatEntity chat = chatService.findChatById(id);

        List<MessageEntity> messages = chatService.getMessages(chat.getId());
        List<ParticipantEntity> participants = chat.getParticipants();

        return ChatResponse.builder()
            .id(chat.getId())
            .name(chat.getName())
            .type(chat.getType())
            .messages(messageService.toMessageResponse(messages))
            .participants(participantService.toParticipantResponse(participants))
            .build();
    }


    @MutationMapping
    public ChatResponse createChat(@Valid @Argument CreateChatRequest chat) {

        ChatEntity chatEntity = ChatEntity.builder()
            .type(chat.type())
            .name(chat.name())
            .participants(new ArrayList<>())
            .build();

        ChatEntity createdChat = chatService.createChat(chatEntity);
        log.info("chat created: {}", chat);


        return ChatResponse.builder()
            .id(createdChat.getId())
            .name(createdChat.getName())
            .type(createdChat.getType())
            .messages(new ArrayList<>())
            .participants(new ArrayList<>())
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
    public Flux<ParticipantChatEventResponse> chatParticipantEvent(@Argument String chatId) {
        return participantService.fetchEvents().filter(event -> event.chatId().equals(chatId));
    }

}
