package com.example.crazy_chat.domains.chat;

import com.example.crazy_chat.domains.message.MessageEntity;
import com.example.crazy_chat.domains.participant.ParticipantEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document("chats")
@NoArgsConstructor
@AllArgsConstructor
public class ChatEntity {

    @Id
    private String id;
    private String name;
    private ChatType type;
    private List<ParticipantEntity> participants;

    @CreatedDate
    private LocalDateTime createdAt;

    @CreatedBy
    private String createdBy;

    public enum ChatType {
        PRIVATE,
        GROUP
    }
}


