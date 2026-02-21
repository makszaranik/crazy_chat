package com.example.crazy_chat.dto.message.input;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TextMessageRequest(
    @NotNull String chatId,
    @NotNull String content
) {}
