package com.example.crazy_chat.dto.chat;

import com.example.crazy_chat.domains.chat.ChatEntity;
import com.example.crazy_chat.dto.message.MessageResponse;
import com.example.crazy_chat.dto.participant.output.ParticipantResponse;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record ChatResponse(
    @NotNull String id,
    @NotNull String name,
    @NotNull ChatEntity.ChatType type,
    @NotNull List<MessageResponse> messages,
    @NotNull List<ParticipantResponse> participants
) {}
