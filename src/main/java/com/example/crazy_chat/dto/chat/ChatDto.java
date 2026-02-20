package com.example.crazy_chat.dto.chat;

import com.example.crazy_chat.domains.chat.ChatEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record ChatDto(
    @NotNull String id,
    @NotNull String name,
    @NotNull ChatEntity.ChatType type,
    @NotNull List<String> messageIds,
    @NotNull List<String> participantIds
) {}
