package com.example.crazy_chat.security;

import com.example.crazy_chat.domains.participant.ParticipantEntity;
import com.example.crazy_chat.service.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OidcUserMapperService extends OidcUserService {

    private final ParticipantService participantService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser loadedUser = super.loadUser(userRequest);

        String username = loadedUser.getAttribute("name");

        ParticipantEntity participant = participantService.fetchParticipantByUsername(username)
            .orElseGet(() -> participantService.save(
                ParticipantEntity.builder()
                    .username(username)
                    .build()
            ));

        return new CustomOidcUser(participant, loadedUser);
    }
}
