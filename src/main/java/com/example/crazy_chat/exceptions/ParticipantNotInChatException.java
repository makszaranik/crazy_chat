package com.example.crazy_chat.exceptions;

public class ParticipantNotInChatException extends RuntimeException {
    public ParticipantNotInChatException(String message) {
        super(message);
    }
}
