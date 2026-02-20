package com.example.crazy_chat.domains.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@Document("messages")
@NoArgsConstructor
@AllArgsConstructor
public abstract sealed class MessageEntity
        permits TextMessageEntity, FileMessageEntity {

    @Id
    private String id;
    private String chatId;
    private String senderId;

    @CreatedDate
    private LocalDateTime createdAt;
}
