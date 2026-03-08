package com.example.crazy_chat.dto.participant.input;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ParticipantChatEventRequest(
    @NotNull String chatId,
    @NotNull EventType event
) {
    public enum EventType {
        JOIN,
        LEAVE,
    }
}

