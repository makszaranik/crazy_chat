package com.example.crazy_chat.security;

import com.example.crazy_chat.domains.participant.ParticipantEntity;
import com.example.crazy_chat.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserMapperService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final ParticipantService participantService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User loadedUser = defaultOAuth2UserService.loadUser(userRequest);

        String email = loadedUser.getAttribute("email");
        String username = loadedUser.getAttribute("name");

        ParticipantEntity participant = participantService.fetchParticipantByUsername(username)
            .orElseGet(() -> participantService.save(
                ParticipantEntity.builder()
                    .username(username)
                    .build()
            ));

        return new CustomOAuth2Participant(participant, loadedUser);
    }

}
