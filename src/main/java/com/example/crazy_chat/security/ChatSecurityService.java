package com.example.crazy_chat.security;

import com.example.crazy_chat.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatSecurityService {

    private final ChatService chatService;

    public boolean participantInChat(String chatId, Authentication authentication) {
        CustomOidcUser participant = (CustomOidcUser) authentication.getPrincipal();
        return chatService.isParticipantInChat(chatId, participant.getId());
    }

}