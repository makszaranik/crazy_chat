package com.example.crazy_chat.service.media;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaTypeResolver {

    private final Tika tika;

    public String resolveMediaType(byte[] file) {
        return tika.detect(file);
    }

}
