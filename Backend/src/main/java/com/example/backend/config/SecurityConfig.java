
package com.example.backend.config;

import com.example.backend.model.AppUser;
import com.example.backend.model.UserSession;
import com.example.backend.repository.AppUserRepository;
import com.example.backend.service.AdminRoleOAuth2UserService;
import com.example.backend.service.UserSessionService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.time.LocalDateTime;
import java.util.Locale;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String SESSION_ID_KEY = "buzzflowSessionId";

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AdminRoleOAuth2UserService adminRoleOAuth2UserService,
            AppUserRepository appUserRepository,
            UserSessionService userSessionService
    ) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/health",
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(adminRoleOAuth2UserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            if (!(authentication.getPrincipal() instanceof OAuth2User googleUser)) {
                                response.sendError(
                                        HttpStatus.UNAUTHORIZED.value(),
                                        "Google user information is unavailable"
                                );
                                return;
                            }

                            String email = googleUser.getAttribute("email");

                            if (email == null || email.isBlank()) {
                                response.sendError(
                                        HttpStatus.UNAUTHORIZED.value(),
                                        "Google account email is missing"
                                );
                                return;
                            }

                            email = email.trim().toLowerCase(Locale.ROOT);

                            final String userEmail = email;

                            AppUser appUser = appUserRepository
                                    .findByEmail(userEmail)
                                    .orElseGet(AppUser::new);

                            appUser.setEmail(userEmail);

                            String name = googleUser.getAttribute("name");
                            appUser.setName(
                                    name == null || name.isBlank() ? userEmail : name
                            );

                            String picture = googleUser.getAttribute("picture");
                            appUser.setPictureUrl(picture);
                            appUser.setLastLoginAt(LocalDateTime.now());

                            boolean isAdmin = !adminEmail.isBlank()
                                    && userEmail.equalsIgnoreCase(adminEmail.trim());

                            appUser.setRole(isAdmin ? "ADMIN" : "USER");

                            appUser = appUserRepository.saveAndFlush(appUser);

                            UserSession newSession = userSessionService.startSession(
                                    appUser,
                                    request.getRemoteAddr(),
                                    request.getHeader("User-Agent")
                            );

                            HttpSession httpSession = request.getSession(true);
                            httpSession.setAttribute(
                                    SESSION_ID_KEY,
                                    newSession.getId()
                            );

                            response.sendRedirect(frontendUrl);
                        })
                )

                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            HttpSession session = request.getSession(false);

                            if (session != null) {
                                Object sessionId =
                                        session.getAttribute(SESSION_ID_KEY);

                                if (sessionId instanceof Long id) {
                                    try {
                                        userSessionService.endSession(id);
                                    } catch (IllegalArgumentException ignored) {
                                        // Session was already removed or ended.
                                    }
                                }
                            }
                        })
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.setStatus(HttpStatus.OK.value())
                        )
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}

