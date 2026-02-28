package com.example.crazy_chat.dto.message.output;

public sealed interface MessageResponse permits FileMessageResponse, TextMessageResponse {
    String id();
    String chatId();
    String senderId();
}
