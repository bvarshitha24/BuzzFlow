
package com.example.backend.service;

import com.example.backend.model.AppUser;
import com.example.backend.model.UserSession;
import com.example.backend.repository.UserSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserSessionService {

    private final UserSessionRepository sessionRepository;

    public UserSessionService(
            UserSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public UserSession startSession(
            AppUser user, String ipAddress, String userAgent) {

        UserSession session = new UserSession();
        session.setUser(user);
        session.setLoginAt(LocalDateTime.now());
        session.setLastActivityAt(LocalDateTime.now());
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setActive(true);

        return sessionRepository.save(session);
    }

    public List<UserSession> getActiveSessions() {
        return sessionRepository.findByActiveTrue();
    }

    @Transactional
    public void endSession(Long sessionId) {
        UserSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Session not found"));

        session.setActive(false);
        session.setLogoutAt(LocalDateTime.now());
        sessionRepository.save(session);
    }
}
