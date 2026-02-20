package com.example.crazy_chat.controller;

import com.example.crazy_chat.domains.chat.ChatEntity;
import com.example.crazy_chat.domains.message.FileMessageEntity;
import com.example.crazy_chat.domains.message.MessageEntity;
import com.example.crazy_chat.domains.message.TextMessageEntity;
import com.example.crazy_chat.dto.chat.ChatDto;
import com.example.crazy_chat.dto.chat.CreateChatDto;
import com.example.crazy_chat.dto.message.FileMessageDto;
import com.example.crazy_chat.dto.message.MessageDto;
import com.example.crazy_chat.dto.message.TextMessageDto;
import com.example.crazy_chat.exceptions.ChatNotFoundException;
import com.example.crazy_chat.service.ChatService;
import com.example.crazy_chat.service.EventService;
import com.example.crazy_chat.service.MessagePublisherService;
import com.example.crazy_chat.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final RabbitTemplate rabbitTemplate;
    private final MessagePublisherService messagePublisherService;
    private final ParticipantService participantService;


    @MutationMapping
    public ChatDto createChat(@Argument CreateChatDto chat) {

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
    public TextMessageDto sendTextMessage(@Argument TextMessageDto message) {

        TextMessageEntity messageEntity = TextMessageEntity.builder()
            .chatId(message.chatId())
            .senderId(message.senderId())
            .content(message.content())
            .build();

        TextMessageEntity savedMessage = messagePublisherService.saveMessage(messageEntity);
        chatService.addMessageToChat(message.chatId(), savedMessage);

        rabbitTemplate.convertAndSend(
            EventService.MESSAGE_EXCHANGE,
            EventService.MESSAGE_QUEUE,
            savedMessage
        );

        log.info("sent message: {}", savedMessage);

        return TextMessageDto.builder()
            .id(savedMessage.getId())
            .chatId(savedMessage.getChatId())
            .senderId(participantService.getCurrentParticipant().getId())
            .content(savedMessage.getContent())
            .build();
    }

    /*
    @SubscriptionMapping
    public Flux<MessageDto> messageSendEvent(@Argument String chatId) {
        return messagePublisherService.fetchMessages()
            .filter(message -> message.getChatId().equals(chatId))
            .map(message -> switch (message) {
                case TextMessageEntity messageEntity -> TextMessageDto.builder()
                    .id(messageEntity.getId())
                    .chatId(messageEntity.getChatId())
                    .content(messageEntity.getContent())
                    .senderId(messageEntity.getSenderId())
                    .build();

                case FileMessageEntity messageEntity -> FileMessageDto.builder()
                    .id(messageEntity.getId())
                    .chatId(messageEntity.getChatId())
                    .senderId(messageEntity.getSenderId())
                    .fileId(messageEntity.getS3FileId())
                    .build();
            });
    }
     */

    @SubscriptionMapping
    public Flux<MessageEntity> messageSendEvent(@Argument String chatId) {
        return messagePublisherService.fetchMessages()
            .filter(message -> message.getChatId().equals(chatId));
    }



    /*
    @SubscriptionMapping
    public Flux<MessageDto> messageSendEvent(@Argument String chatId) {
        return messagePublisherService.fetchMessages()
            .filter(message -> message.getChatId().equals(chatId))
            .map(message -> switch (message) {
                case TextMessageEntity messageEntity -> TextMessageDto.builder()
                    .id(messageEntity.getId())
                    .chatId(messageEntity.getChatId())
                    .content(messageEntity.getContent())
                    .senderId(messageEntity.getSenderId())
                    .build();

                case FileMessageEntity messageEntity -> FileMessageDto.builder()
                    .id(messageEntity.getId())
                    .chatId(messageEntity.getChatId())
                    .senderId(messageEntity.getSenderId())
                    .fileId(messageEntity.getS3FileId())
                    .contentType(messageEntity.getContentType())
                    .build();
            });
    }

     */



}
