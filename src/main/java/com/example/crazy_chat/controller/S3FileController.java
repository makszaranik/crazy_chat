package com.example.crazy_chat.controller;

import com.example.crazy_chat.domains.file.FileMetadataEntity;
import com.example.crazy_chat.dto.file.input.CompleteMultipartRequest;
import com.example.crazy_chat.dto.file.input.InitMultipartUploadRequest;
import com.example.crazy_chat.dto.file.output.MultipartUploadResponse;
import com.example.crazy_chat.service.FileMetadataService;
import com.example.crazy_chat.service.S3FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Service
@Controller
@RequiredArgsConstructor
public class S3FileController {

    private final S3FileService s3FileService;
    private final FileMetadataService fileMetadataService;


    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public MultipartUploadResponse initiateUpload(@Valid @Argument InitMultipartUploadRequest request) {
        FileMetadataEntity metadataEntity = FileMetadataEntity.builder()
            .contentLength(request.fileSize())
            .contentType(request.contentType())
            .build();

        FileMetadataEntity fileMetadataEntity = fileMetadataService.save(metadataEntity);
        return s3FileService.initiateUpload(fileMetadataEntity, request);
    }


    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean completeUpload(@Valid @Argument CompleteMultipartRequest request) {
        FileMetadataEntity metadataEntity = fileMetadataService.findMetadataEntityById(request.fileId());
        s3FileService.completeUpload(metadataEntity, request);
        return true;
    }


    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public String getDownloadLink(@Argument String fileId) {
        return s3FileService.getDownloadLink(fileId);
    }


    @SneakyThrows
    @PostMapping("file/upload")
    public void uploadFile(@RequestParam("file") MultipartFile file) {
        FileMetadataEntity metadataEntity = FileMetadataEntity.builder()
            .contentLength(file.getBytes().length)
            .contentType(file.getContentType())
            .build();

        s3FileService.uploadFile(metadataEntity, file);
    }

}
