package com.example.crazy_chat.dto.chat;

import com.example.crazy_chat.domains.chat.ChatEntity;
import com.example.crazy_chat.dto.message.output.MessageResponse;
import com.example.crazy_chat.dto.participant.output.ParticipantResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record ChatResponse(
    String id,
    String name,
    ChatEntity.ChatType type,
    List<MessageResponse> messages,
    List<ParticipantResponse> participants
) {}
