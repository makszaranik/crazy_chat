package com.example.crazy_chat.dto.participant.output;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ParticipantEventResponse(
    @NotNull String chatId,
    @NotNull String participantId,
    @NotNull EventType event
) {
    public enum EventType {
        JOIN,
        LEAVE,
    }
}

