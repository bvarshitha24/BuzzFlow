
package com.example.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AdminRoleOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final CustomOAuth2UserService delegate;
    private final String adminEmail;

    public AdminRoleOAuth2UserService(
            CustomOAuth2UserService delegate,
            @Value("${app.admin.email:}") String adminEmail) {
        this.delegate = delegate;
        this.adminEmail = adminEmail.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User user = delegate.loadUser(request);

        String email = user.getAttribute("email");
        List<GrantedAuthority> authorities =
                new ArrayList<>(user.getAuthorities());

        if (email != null && !adminEmail.isBlank()
                && email.trim().equalsIgnoreCase(adminEmail)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        String nameAttributeKey = user.getAttribute("sub") != null
                ? "sub" : "email";

        return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                authorities, user.getAttributes(), nameAttributeKey);
    }
}
