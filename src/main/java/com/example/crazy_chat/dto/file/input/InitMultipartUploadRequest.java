package com.example.crazy_chat.dto.file.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InitMultipartUploadRequest(
    @NotNull String fileName,
    @Positive int fileSize,
    @NotNull String contentType
) {}