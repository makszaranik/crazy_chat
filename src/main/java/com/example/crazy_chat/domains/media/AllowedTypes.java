package com.example.crazy_chat.domains.media;

import java.util.Arrays;

public enum AllowedTypes {
    JPEG("image/jpeg"),
    PNG("image/png"),
    PDF("application/pdf");

    final String mimeType;

    AllowedTypes(String mimeType) {
        this.mimeType = mimeType;
    }

    public static boolean isAllowedType(String contentType) {
        return Arrays.stream(AllowedTypes.values())
            .anyMatch(type -> type.mimeType.equalsIgnoreCase(contentType));
    }
}