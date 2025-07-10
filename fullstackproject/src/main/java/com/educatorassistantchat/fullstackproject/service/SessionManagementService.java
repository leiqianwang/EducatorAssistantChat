package com.educatorassistantchat.fullstackproject.service;

import com.educatorassistantchat.fullstackproject.dto.ChatRequest;
import com.educatorassistantchat.fullstackproject.dto.ChatSession;
import com.educatorassistantchat.fullstackproject.model.ChatSessionEntity;
import com.educatorassistantchat.fullstackproject.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service responsible for chat session lifecycle management
 * Handles session creation, retrieval, updates, and context management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SessionManagementService {

    private final ChatSessionRepository sessionRepository;

    /**
     * Get or create a chat session based on the request
     */
    @Transactional
    public ChatSessionEntity getOrCreateSession(ChatRequest request) {
        if (request.getSessionId() != null && !request.getSessionId().isEmpty()) {
            return sessionRepository.findBySessionIdAndUserId(request.getSessionId(), request.getUserId())
                .orElseGet(() -> createNewSession(request));
        } else {
            return createNewSession(request);
        }
    }

    /**
     * Create a new chat session
     */
    @Transactional
    public ChatSessionEntity createNewSession(ChatRequest request) {
        String sessionId = generateSessionId();
        String preferredLanguage = extractPreferredLanguage(request);
        String title = generateSessionTitle(request.getPrompt());

        ChatSessionEntity session = ChatSessionEntity.builder()
            .sessionId(sessionId)
            .userId(request.getUserId())
            .title(title)
            .isActive(true)
            .preferredLanguage(preferredLanguage)
            .build();

        ChatSessionEntity savedSession = sessionRepository.save(session);
        log.info("Created new session {} for user {}", sessionId, request.getUserId());
        return savedSession;
    }

    /**
     * Update session activity and context
     */
    @Transactional
    public void updateSessionActivity(ChatSessionEntity session, ChatRequest request) {
        session.setLastActivity(LocalDateTime.now());
        session.setLastActionType(request.getActionType());

        // Update subject from educational context
        if (request.getEducationalContext() != null &&
            request.getEducationalContext().containsKey("subject")) {
            String subject = (String) request.getEducationalContext().get("subject");
            session.setCurrentSubject(subject);
            log.debug("Updated session {} subject to: {}", session.getSessionId(), subject);
        }

        // Update education level if provided
        if (request.getEducationalContext() != null &&
            request.getEducationalContext().containsKey("gradeLevel")) {
            String gradeLevel = (String) request.getEducationalContext().get("gradeLevel");
            session.setEducationLevel(gradeLevel);
        }

        sessionRepository.save(session);
    }

    /**
     * Get user's active sessions
     */
    public List<ChatSession> getUserActiveSessions(String userId) {
        List<ChatSessionEntity> sessions = sessionRepository
            .findByUserIdAndIsActiveTrueOrderByLastActivityDesc(userId);

        return sessions.stream()
            .map(this::convertToSessionDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get session by ID and user ID
     */
    public ChatSessionEntity getSessionByIdAndUser(String sessionId, String userId) {
        return sessionRepository.findBySessionIdAndUserId(sessionId, userId)
            .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
    }

    /**
     * Deactivate a session
     */
    @Transactional
    public void deactivateSession(String sessionId, String userId) {
        ChatSessionEntity session = getSessionByIdAndUser(sessionId, userId);
        session.setIsActive(false);
        sessionRepository.save(session);
        log.info("Deactivated session {} for user {}", sessionId, userId);
    }

    /**
     * Generate a unique session ID
     */
    private String generateSessionId() {
        return "session_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Extract preferred language from request
     */
    private String extractPreferredLanguage(ChatRequest request) {
        // Check action params first
        if (request.getActionParams() != null &&
            request.getActionParams().containsKey("targetLanguage")) {
            return (String) request.getActionParams().get("targetLanguage");
        }

        // Check educational context
        if (request.getEducationalContext() != null &&
            request.getEducationalContext().containsKey("language")) {
            return (String) request.getEducationalContext().get("language");
        }

        // Default to English
        return "English";
    }

    /**
     * Generate session title from prompt
     */
    private String generateSessionTitle(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return "New Chat Session";
        }

        String cleanPrompt = prompt.trim();
        if (cleanPrompt.length() <= 50) {
            return cleanPrompt;
        }

        // Find a good break point
        String truncated = cleanPrompt.substring(0, 47);
        int lastSpace = truncated.lastIndexOf(' ');
        if (lastSpace > 20) {
            return truncated.substring(0, lastSpace) + "...";
        }

        return truncated + "...";
    }

    /**
     * Convert session entity to DTO
     */
    private ChatSession convertToSessionDTO(ChatSessionEntity entity) {
        return ChatSession.builder()
            .sessionId(entity.getSessionId())
            .userId(entity.getUserId())
            .title(entity.getTitle())
            .createdAt(entity.getCreatedAt())
            .lastActivity(entity.getLastActivity())
            .isActive(entity.getIsActive())
            .build();
    }
}
