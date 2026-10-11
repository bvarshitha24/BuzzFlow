
package com.example.backend.controller;

import com.example.backend.model.UserSession;
import com.example.backend.service.UserSessionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserSessionService sessionService;

    public AdminController(UserSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/sessions")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserSession> getActiveSessions() {
        return sessionService.getActiveSessions();
    }
}
