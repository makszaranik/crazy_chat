package com.example.crazy_chat.dto.participant;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ParticipantEventDto(
    @NotNull String chatId,
    @NotNull String participantId,
    @NotNull EventType event
) {
    public enum EventType {
        JOIN,
        LEAVE,
    }
}

