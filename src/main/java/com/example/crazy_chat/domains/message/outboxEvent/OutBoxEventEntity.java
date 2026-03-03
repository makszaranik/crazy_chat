package com.example.crazy_chat.domains.message.outboxEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document("outbox")
@AllArgsConstructor
@NoArgsConstructor
public class OutBoxEventEntity {

    @Id
    private String id;
    private String messageId;
    private AggregateType status;

    @CreatedDate
    private LocalDateTime createdAt;

    public enum AggregateType {
        CREATED,
        DELIVERED
    }
}
