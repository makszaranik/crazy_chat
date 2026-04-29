package com.example.crazy_chat.service.media;

import com.example.crazy_chat.config.s3.S3ClientConfig;
import com.example.crazy_chat.domains.media.AllowedTypes;
import com.example.crazy_chat.domains.media.FileMetadataEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaTypeValidator {

    private final S3Client s3Client;
    private final FileMetadataService fileMetadataService;
    private final S3ClientConfig.S3PropertiesHolder s3PropertiesHolder;
    private final MediaTypeResolver mediaTypeResolver;
    private final S3FileService s3FileService;


    @Scheduled(cron = "0 */1 * * * *")
    public void validateMediaType() {
        fileMetadataService.findAllPending().forEach(media -> {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3PropertiesHolder.bucket())
                .key(media.getFileId())
                .range("bytes=0-4096")
                .build();

            try (var responseInputStream = s3Client.getObject(getObjectRequest)) {
                String mediaType = mediaTypeResolver.resolveMediaType(responseInputStream.readAllBytes());

                if (AllowedTypes.isAllowedType(mediaType)) {
                    media.setStatus(FileMetadataEntity.ValidationStatus.VALID);
                    media.setContentType(mediaType);
                } else {
                    s3FileService.deleteFile(media.getFileId());
                    media.setStatus(FileMetadataEntity.ValidationStatus.REJECTED);
                    log.warn("File with id {} not allowed. Detected type: {}", media.getFileId(), mediaType);
                }

                fileMetadataService.save(media);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
