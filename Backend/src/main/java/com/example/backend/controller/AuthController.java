
package com.example.backend.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public Map<String, Object> currentUser(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof OAuth2User user)) {
            return Map.of("authenticated", false);
        }

        String email = user.getAttribute("email");
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return Map.of(
                "authenticated", true,
                "name", safe(user.getAttribute("name")),
                "email", safe(email),
                "picture", safe(user.getAttribute("picture")),
                "admin", isAdmin
        );
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
