package com.example.crazy_chat.security;

import com.example.crazy_chat.domains.participant.ParticipantEntity;
import com.example.crazy_chat.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2Participant implements OAuth2User {

    private final ParticipantEntity participant;
    private final OAuth2User oAuth2User;

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_PARTICIPANT"));
    }

    @Override
    public String getName() {
        return participant.getUsername();
    }

    public String getId() {
        return participant.getId();
    }
}
