package com.example.crazy_chat.dto.file.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CompleteMultipartRequest(
    @NotNull String fileId,
    @NotNull String uploadId,
    @NotNull List<CompletedPart> parts
) {
    public record CompletedPart(
        @Positive int partNumber,
        @NotNull String etag
    ) {}
}