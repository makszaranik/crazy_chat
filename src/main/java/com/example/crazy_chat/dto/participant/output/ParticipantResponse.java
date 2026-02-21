package com.example.crazy_chat.dto.participant.output;

import lombok.Builder;

@Builder
public record ParticipantResponse(
    String id,
    String username
) {}
