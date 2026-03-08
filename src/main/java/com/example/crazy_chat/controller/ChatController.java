package com.example.crazy_chat.controller;

import com.example.crazy_chat.domains.chat.ChatEntity;
import com.example.crazy_chat.domains.message.FileMessageEntity;
import com.example.crazy_chat.domains.message.MessageEntity;
import com.example.crazy_chat.domains.message.TextMessageEntity;
import com.example.crazy_chat.domains.participant.ParticipantEntity;
import com.example.crazy_chat.dto.chat.ChatResponse;
import com.example.crazy_chat.dto.chat.CreateChatRequest;
import com.example.crazy_chat.dto.message.input.FileMessageRequest;
import com.example.crazy_chat.dto.message.output.FileMessageResponse;
import com.example.crazy_chat.dto.message.output.MessageResponse;
import com.example.crazy_chat.dto.message.input.TextMessageRequest;
import com.example.crazy_chat.dto.message.output.TextMessageResponse;
import com.example.crazy_chat.dto.participant.input.ParticipantChatEventRequest;
import com.example.crazy_chat.dto.participant.output.ParticipantChatEventResponse;
import com.example.crazy_chat.dto.participant.output.ParticipantResponse;
import com.example.crazy_chat.service.chat.ChatService;
import com.example.crazy_chat.service.message.EventService;
import com.example.crazy_chat.service.message.MessageMapperService;
import com.example.crazy_chat.service.message.MessageService;
import com.example.crazy_chat.service.participant.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final RabbitTemplate rabbitTemplate;
    private final MessageService messageService;
    private final ParticipantService participantService;
    private final MessageMapperService messageMapperService;

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public ChatResponse chat(@Argument String id) {
        ChatEntity chat = chatService.findChatById(id);

        return ChatResponse.builder()
            .id(chat.getId())
            .name(chat.getName())
            .type(chat.getType())
            .build();
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<ChatResponse> chats() {
        return chatService.fetchAllChats().stream()
            .map(chat -> ChatResponse.builder()
                .id(chat.getId())
                .name(chat.getName())
                .type(chat.getType())
                .build())
            .toList();
    }


    @BatchMapping(typeName = "Chat", field = "messages")
    public Map<ChatResponse, List<MessageResponse>> batchMessages(List<ChatResponse> chats){
        List<String> chatIds = chats.stream().map(ChatResponse::id).toList();
        Map<String, List<MessageResponse>> messagesByChatIds = messageService.fetchMessagesByChatIds(chatIds);

        return chats.stream()
            .collect(Collectors.toMap(
                Function.identity(),
                chat -> messagesByChatIds.getOrDefault(chat.id(), List.of())
            ));
    }


    @BatchMapping(typeName = "Chat", field = "participants")
    public Map<ChatResponse, List<ParticipantResponse>> batchParticipants(List<ChatResponse> chats){
        List<String> chatIds = chats.stream().map(ChatResponse::id).toList();
        Map<String, List<ParticipantResponse>> participantsByChatIds = participantService.fetchParticipantsByChatIds(chatIds);

        return chats.stream()
            .collect(Collectors.toMap(
                Function.identity(),
                chat -> participantsByChatIds.getOrDefault(chat.id(), List.of()))
            );
    }


    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public ChatResponse createChat(@Valid @Argument CreateChatRequest chat) {
        ChatEntity chatEntity = ChatEntity.builder()
            .type(chat.type())
            .name(chat.name())
            .participants(new HashSet<>())
            .build();

        ChatEntity createdChat = chatService.createChat(chatEntity);
        log.debug("chat created: {}", chat);

        return ChatResponse.builder()
            .id(createdChat.getId())
            .name(createdChat.getName())
            .type(createdChat.getType())
            .build();
    }


    @MutationMapping
    @PreAuthorize("@chatSecurityService.participantInChat(#message.chatId(), authentication)")
    public TextMessageResponse sendTextMessage(@Valid @Argument TextMessageRequest message) {
        TextMessageEntity messageEntity = TextMessageEntity.builder()
            .chatId(message.chatId())
            .senderId(participantService.getCurrentParticipant().getId())
            .content(message.content())
            .build();

        TextMessageEntity savedMessage = (TextMessageEntity) messageService.sendMessageWithOutbox(messageEntity);
        chatService.addMessageToChat(message.chatId(), savedMessage);

        return TextMessageResponse.builder()
            .id(savedMessage.getId())
            .chatId(savedMessage.getChatId())
            .senderId(savedMessage.getSenderId())
            .content(savedMessage.getContent())
            .build();
    }


    @MutationMapping
    @PreAuthorize("@chatSecurityService.participantInChat(#message.chatId(), authentication)")
    public FileMessageResponse sendFileMessage(@Valid @Argument FileMessageRequest message){
        FileMessageEntity messageEntity = FileMessageEntity.builder().
            chatId(message.chatId())
            .senderId(participantService.getCurrentParticipant().getId())
            .s3FileId(message.fileId())
            .build();

        FileMessageEntity savedMessage = (FileMessageEntity) messageService.sendMessageWithOutbox(messageEntity);
        chatService.addMessageToChat(message.chatId(), savedMessage);

        return FileMessageResponse.builder()
            .id(savedMessage.getId())
            .chatId(savedMessage.getChatId())
            .senderId(savedMessage.getSenderId())
            .fileId(savedMessage.getS3FileId())
            .build();

    }


    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean chatParticipantAction(@Valid @Argument ParticipantChatEventRequest chatEvent) {
        switch (chatEvent.event()) {
            case JOIN -> chatService.addParticipantToChat(
                chatEvent.chatId(),
                participantService.getCurrentParticipant().getId()
            );
            
            case LEAVE -> chatService.removeParticipantFromChat(
                chatEvent.chatId(),
                participantService.getCurrentParticipant().getId()
            );
        }

        rabbitTemplate.convertAndSend(
            EventService.PARTICIPANT_EVENT_EXCHANGE,
            EventService.PARTICIPANT_EVENT_QUEUE,
            chatEvent
        );

        return true;
    }


    @SubscriptionMapping
    @PreAuthorize("@chatSecurityService.participantInChat(#chatId, authentication)")
    public Flux<MessageResponse> messageSendEvent(@Argument String chatId) {
        return messageService.fetchEvents()
            .filter(event -> event.getChatId().equals(chatId))
            .map(messageMapperService::toMessageResponse);
    }


    @SubscriptionMapping
    @PreAuthorize("@chatSecurityService.participantInChat(#chatId, authentication)")
    public Flux<ParticipantChatEventResponse> chatParticipantEvent(@Argument String chatId) {
        return participantService.fetchEvents().filter(event -> event.chatId().equals(chatId));
    }

}
