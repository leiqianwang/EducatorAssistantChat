package com.educatorassistantchat.fullstackproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Chat Session DTO for managing chat sessions and history for Educators
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession {

    private String sessionId;

    private String userId;

    private String title; // Auto-generated or user-defined session title

    private LocalDateTime createdAt;

    private LocalDateTime lastActivity;

    private List<ChatMessage> messages;

    private SessionContext context;

    private Boolean isActive;

    /**
     * Individual chat message within a session
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        private String messageId;
        private String content;
        private String sender; // USER, ASSISTANT
        private LocalDateTime timestamp;
        private String actionType;
        private ChatResponse.ActionMetadata actionMetadata;
    }

    /**
     * Session context for maintaining conversation state
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionContext {
        private String currentSubject;
        private String preferredLanguage;
        private String educationLevel;
        private List<String> topics; // Topics discussed in this session
        private String lastActionType;
    }
}
