package com.example.crazy_chat.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;

@Builder
public record FileMessageDto(
    @NotNull String chatId,
    @NotNull String fileId
) implements MessageDto {}
