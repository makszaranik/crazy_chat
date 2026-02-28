package com.example.crazy_chat.domains.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.MimeType;

import java.time.LocalDateTime;

@Data
@Builder
@Document("file")
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataEntity {

    @Id
    private String fileId;
    private Long fileSize;
    private String contentType;

    @CreatedBy
    private String uploaderId;

    @CreatedDate
    private LocalDateTime uploadedDate;
}
