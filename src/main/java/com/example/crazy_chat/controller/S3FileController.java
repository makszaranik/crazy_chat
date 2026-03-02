package com.example.crazy_chat.controller;

import com.example.crazy_chat.domains.file.FileMetadataEntity;
import com.example.crazy_chat.dto.file.input.CompleteMultipartRequest;
import com.example.crazy_chat.dto.file.input.InitMultipartUploadRequest;
import com.example.crazy_chat.dto.file.output.MultipartUploadResponse;
import com.example.crazy_chat.service.FileMetadataService;
import com.example.crazy_chat.service.S3FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Service
@Controller
@RequiredArgsConstructor
public class S3FileController {

    private final S3FileService s3FileService;
    private final FileMetadataService fileMetadataService;


    @MutationMapping
    public MultipartUploadResponse initiateUpload(@Valid @Argument InitMultipartUploadRequest request) {
        FileMetadataEntity metadataEntity = FileMetadataEntity.builder()
            .contentLength(request.fileSize())
            .contentType(request.contentType())
            .build();

        FileMetadataEntity fileMetadataEntity = fileMetadataService.save(metadataEntity);
        return s3FileService.initiateUpload(fileMetadataEntity, request);
    }


    @MutationMapping
    public Boolean completeUpload(@Valid @Argument CompleteMultipartRequest request) {
        FileMetadataEntity metadataEntity = fileMetadataService.findMetadataEntityById(request.fileId());
        s3FileService.completeUpload(metadataEntity, request);
        return true;
    }


    @QueryMapping
    public String getDownloadLink(@Argument String fileId) {
        return s3FileService.getDownloadLink(fileId);
    }

}
