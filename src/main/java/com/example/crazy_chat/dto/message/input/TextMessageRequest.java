package com.example.crazy_chat.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;

@Builder
public record TextMessageDto(
    @Null String id,
    @NotNull String chatId,
    @Null String senderId,
    @NotNull String content
) implements MessageDto {}
