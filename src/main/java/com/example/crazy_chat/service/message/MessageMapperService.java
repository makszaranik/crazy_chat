package com.example.crazy_chat.service.message;

import com.example.crazy_chat.domains.message.FileMessageEntity;
import com.example.crazy_chat.domains.message.MessageEntity;
import com.example.crazy_chat.domains.message.TextMessageEntity;
import com.example.crazy_chat.dto.message.output.FileMessageResponse;
import com.example.crazy_chat.dto.message.output.MessageResponse;
import com.example.crazy_chat.dto.message.output.TextMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageMapperService {

    public List<MessageResponse> toMessageResponse(List<MessageEntity> messages) {
        return messages.stream().map(this::toMessageResponse).toList();
    }

    public MessageResponse toMessageResponse(MessageEntity message) {
        return switch (message) {
            case TextMessageEntity textMessage -> TextMessageResponse.builder()
                .id(textMessage.getId())
                .chatId(textMessage.getChatId())
                .senderId(textMessage.getSenderId())
                .content(textMessage.getContent())
                .build();

            case FileMessageEntity textMessage -> FileMessageResponse.builder()
                .id(textMessage.getId())
                .chatId(textMessage.getChatId())
                .senderId(textMessage.getSenderId())
                .fileId(textMessage.getS3FileId())
                .build();
        };
    }


}
