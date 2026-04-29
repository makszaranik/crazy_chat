package com.example.crazy_chat.service.media;

import com.example.crazy_chat.config.s3.S3ClientConfig;
import com.example.crazy_chat.domains.media.FileMetadataEntity;
import com.example.crazy_chat.dto.file.input.CompleteMultipartRequest;
import com.example.crazy_chat.dto.file.input.InitMultipartUploadRequest;
import com.example.crazy_chat.dto.file.output.MultipartUploadResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3FileService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3ClientConfig.S3PropertiesHolder s3PropertiesHolder;

    public MultipartUploadResponse initiateUpload(FileMetadataEntity fileMetadata, InitMultipartUploadRequest multipartUploadRequest) {
        CreateMultipartUploadRequest uploadRequest = CreateMultipartUploadRequest.builder()
            .bucket(s3PropertiesHolder.bucket())
            .key(fileMetadata.getFileId())
            .contentType(fileMetadata.getContentType())
            .build();

        CreateMultipartUploadResponse uploadResponse = s3Client.createMultipartUpload(uploadRequest);
        long partsNumber = getPartsNumber(multipartUploadRequest.fileSize());

        List<String> presignedUrls = new ArrayList<>();
        for (int i = 0; i < partsNumber; i++) {
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .bucket(s3PropertiesHolder.bucket())
                .key(fileMetadata.getFileId())
                .uploadId(uploadResponse.uploadId())
                .partNumber(i + 1)
                .build();

            UploadPartPresignRequest uploadPartPresignRequest = UploadPartPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .uploadPartRequest(uploadPartRequest)
                .build();

            String presignedPartUrl = s3Presigner.presignUploadPart(uploadPartPresignRequest).url().toString();
            presignedUrls.add(presignedPartUrl);
        }

        return MultipartUploadResponse.builder()
            .uploadId(uploadResponse.uploadId())
            .parts(partsNumber)
            .urls(presignedUrls)
            .build();
    }


    public void completeUpload(FileMetadataEntity fileMetadata, CompleteMultipartRequest completeMultipartRequest) {
        List<CompletedPart> completedParts = completeMultipartRequest.parts().stream()
            .map(part -> CompletedPart.builder()
                .partNumber(part.partNumber())
                .eTag(part.etag())
                .build()
            )
            .toList();

        CompletedMultipartUpload multipartUpload = CompletedMultipartUpload.builder()
            .parts(completedParts)
            .build();

        CompleteMultipartUploadRequest uploadRequest = CompleteMultipartUploadRequest.builder()
            .bucket(s3PropertiesHolder.bucket())
            .key(fileMetadata.getFileId())
            .uploadId(completeMultipartRequest.uploadId())
            .multipartUpload(multipartUpload)
            .build();

        s3Client.completeMultipartUpload(uploadRequest);
    }


    public void uploadFile(FileMetadataEntity fileMetadata, MultipartFile file) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(s3PropertiesHolder.bucket())
            .key(fileMetadata.getFileId())
            .contentType(fileMetadata.getContentType())
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
    }


    public String getDownloadLink(String fileId) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .key(fileId)
            .bucket(s3PropertiesHolder.bucket())
            .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10))
            .getObjectRequest(getObjectRequest)
            .build();

        return s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
    }


    private long getPartsNumber(int fileSize) {
        long partSize = DataSize.ofMegabytes(10).toBytes();
        return (fileSize + partSize - 1) / partSize;
    }

    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(s3PropertiesHolder.bucket())
            .key(key)
            .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}
