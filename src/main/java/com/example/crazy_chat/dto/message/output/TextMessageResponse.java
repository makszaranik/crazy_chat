package com.example.crazy_chat.dto.message.output;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TextMessageResponse(
    @NotNull String id,
    @NotNull String chatId,
    @NotNull String senderId,
    @NotNull String content
) implements MessageResponse {}