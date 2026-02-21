package com.example.crazy_chat.dto.message.input;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record FileMessageRequest(
    @NotNull String chatId,
    @NotNull String fileId
) {}
