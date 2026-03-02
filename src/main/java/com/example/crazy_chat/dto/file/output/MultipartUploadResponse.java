package com.example.crazy_chat.dto.file.output;

import lombok.Builder;

import java.util.List;

@Builder
public record MultipartUploadResponse(
    String fileId,
    String uploadId,
    long parts,
    List<String> urls
) {}