package com.educatorassistantchat.fullstackproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Chat Response DTO for returning AI responses to frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {

    private String messageId;

    private String content;

    private String sessionId;

    private LocalDateTime timestamp;

    private String status; // SUCCESS, ERROR, PROCESSING

    private String aiModel; // Model used for generating response

    private List<String> suggestedPrompts; // Dynamic suggested prompts

    private String actionResult; // Result of action-based operations

    private ActionMetadata actionMetadata; // Metadata for actions performed

    private String errorMessage; // Error details if any

    private Integer tokensUsed; // For monitoring AI usage

    /**
     * Nested class for action metadata
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActionMetadata {
        private String actionType;
        private String originalLanguage;
        private String targetLanguage;
        private String summaryType; // SENTENCE, PARAGRAPH, BULLET_POINTS
        private String tone; // For rewrite operations
        private Integer questionCount; // For question generation
    }
}
