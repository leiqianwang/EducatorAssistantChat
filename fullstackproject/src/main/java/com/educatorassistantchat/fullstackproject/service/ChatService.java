package com.educatorassistantchat.fullstackproject.service;

import com.educatorassistantchat.fullstackproject.dto.*;
import com.educatorassistantchat.fullstackproject.model.ActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Refactored ChatService - now acts as a facade to the orchestration service
 * Maintains backward compatibility while delegating to modular services
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatOrchestrationService chatOrchestrationService;
    private final SessionManagementService sessionManagementService;
    private final MessagePersistenceService messagePersistenceService;

    /**
     * Main chat processing method - delegates to orchestration service
     */
    public ChatResponse processChat(ChatRequest request) {
        return chatOrchestrationService.processChat(request);
    }

    /**
     * Get chat history for a session
     */
    public ChatSession getChatHistory(String sessionId, String userId) {
        return messagePersistenceService.getChatHistory(sessionId, userId);
    }

    /**
     * Get user's recent sessions
     */
    public List<ChatSession> getUserSessions(String userId) {
        return sessionManagementService.getUserActiveSessions(userId);
    }
}
