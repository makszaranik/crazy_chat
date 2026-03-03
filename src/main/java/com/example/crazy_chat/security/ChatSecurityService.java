package com.example.crazy_chat.security;

import com.example.crazy_chat.service.ChatService;
import com.example.crazy_chat.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatSecurityService {

    private final ChatService chatService;

    public boolean participantInChat(String chatId, Authentication authentication) {
        CustomOAuth2Participant participant = (CustomOAuth2Participant) authentication.getPrincipal();
        return chatService.isParticipantInChat(chatId, participant.getId());
    }

}
