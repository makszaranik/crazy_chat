package com.example.crazy_chat.dto.chat;

import com.example.crazy_chat.domains.chat.ChatEntity;
import jakarta.validation.constraints.NotNull;

public record CreateChatRequest(
    @NotNull ChatEntity.ChatType type,
    @NotNull String name
) {}
