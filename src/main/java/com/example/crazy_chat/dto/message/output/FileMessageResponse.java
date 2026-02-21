package com.example.crazy_chat.dto.message.output;

import com.example.crazy_chat.dto.message.MessageResponse;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record FileMessageResponse(
    @NotNull String id,
    @NotNull String chatId,
    @NotNull String senderId,
    @NotNull String fileId
) implements MessageResponse {}
