
package com.example.backend.repository;

import com.example.backend.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSessionRepository
        extends JpaRepository<UserSession, Long> {

    List<UserSession> findByActiveTrue();

    List<UserSession> findByUserIdOrderByLoginAtDesc(Long userId);
}
