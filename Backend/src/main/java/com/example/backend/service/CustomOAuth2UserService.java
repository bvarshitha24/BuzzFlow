
package com.example.backend.service;

import com.example.backend.model.AppUser;
import com.example.backend.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AppUserRepository userRepository;
    private final String adminEmail;

    public CustomOAuth2UserService(
            AppUserRepository userRepository,
            @Value("${app.admin.email:}") String adminEmail) {
        this.userRepository = userRepository;
        this.adminEmail = adminEmail.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request)
            throws OAuth2AuthenticationException {

        OAuth2User googleUser = super.loadUser(request);

        String email = googleUser.getAttribute("email");
        String name = googleUser.getAttribute("name");
        String picture = googleUser.getAttribute("picture");
        Boolean verified = googleUser.getAttribute("email_verified");

        if (email == null || !Boolean.TRUE.equals(verified)) {
            throw new OAuth2AuthenticationException(
                    "Google account email is missing or unverified");
        }

        email = email.trim().toLowerCase(Locale.ROOT);

        AppUser appUser = userRepository.findByEmail(email)
                .orElseGet(AppUser::new);

        appUser.setEmail(email);
        appUser.setName(name == null || name.isBlank() ? email : name);
        appUser.setPictureUrl(picture);
        appUser.setLastLoginAt(LocalDateTime.now());

        if (!adminEmail.isBlank() && email.equals(adminEmail)) {
            appUser.setRole("ADMIN");
        } else {
            appUser.setRole("USER");
        }

        userRepository.save(appUser);

        return googleUser;
    }
}
