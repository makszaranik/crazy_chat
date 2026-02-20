package com.example.crazy_chat.domains.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@SuperBuilder
@Document("messages")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class FileMessageEntity extends MessageEntity implements Serializable {
    private String s3FileId;
    private String contentType;
}
