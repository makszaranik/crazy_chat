package com.example.crazy_chat.security;

import com.example.crazy_chat.domains.participant.ParticipantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOidcUser implements OidcUser {

    private final ParticipantEntity participant;
    private final OidcUser oidcUser;

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_PARTICIPANT"));
    }

    @Override
    public String getName() {
        return participant.getId();
    }

    public String getId() {
        return participant.getId();
    }


    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }
}
