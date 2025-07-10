package com.educatorassistantchat.fullstackproject.service;

import com.educatorassistantchat.fullstackproject.dto.ChatRequest;
import com.educatorassistantchat.fullstackproject.model.ChatMessageEntity;
import com.educatorassistantchat.fullstackproject.model.ChatSessionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service responsible for building contextual information for AI prompts
 * Aggregates session history, educational context, and user-specific information
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContextBuilderService {

    private final MessagePersistenceService messagePersistenceService;

    /**
     * Build comprehensive contextual message for AI processing
     */
    public String buildContextualMessage(ChatRequest request, ChatSessionEntity session) {
        StringBuilder contextBuilder = new StringBuilder();

        // Add system context for educator-focused responses
        contextBuilder.append("You are an AI assistant specifically designed for educators. ");
        contextBuilder.append("Provide educational, practical, and actionable responses.\n\n");

        // Add educational context from request
        addEducationalContext(contextBuilder, request);

        // Add session context
        addSessionContext(contextBuilder, session);

        // Add conversation history
        addConversationHistory(contextBuilder, session);

        // Add current prompt
        contextBuilder.append("Current Request: ").append(request.getPrompt());

        String finalContext = contextBuilder.toString();
        log.debug("Built context for session {}: {} characters",
                 session.getSessionId(), finalContext.length());

        return finalContext;
    }

    /**
     * Build enhanced educational context from session and request
     */
    public Map<String, Object> buildEnhancedEducationalContext(ChatRequest request, ChatSessionEntity session) {
        Map<String, Object> enhancedContext = new java.util.HashMap<>();

        // Add request context
        if (request.getEducationalContext() != null) {
            enhancedContext.putAll(request.getEducationalContext());
        }

        // Add session context if not already present
        if (session.getCurrentSubject() != null && !enhancedContext.containsKey("subject")) {
            enhancedContext.put("subject", session.getCurrentSubject());
        }

        if (session.getEducationLevel() != null && !enhancedContext.containsKey("gradeLevel")) {
            enhancedContext.put("gradeLevel", session.getEducationLevel());
        }

        if (session.getPreferredLanguage() != null && !enhancedContext.containsKey("language")) {
            enhancedContext.put("language", session.getPreferredLanguage());
        }

        return enhancedContext;
    }

    /**
     * Build context specifically for action-based requests
     */
    public String buildActionContext(ChatRequest request, ChatSessionEntity session, String actionType) {
        StringBuilder contextBuilder = new StringBuilder();

        // Add action-specific system prompts
        switch (actionType.toUpperCase()) {
            case "TRANSLATE" -> contextBuilder.append("You are a professional educational translator. ");
            case "SUMMARIZE" -> contextBuilder.append("You are an expert at creating educational summaries. ");
            case "REWRITE" -> contextBuilder.append("You are an educational content editor. ");
            case "QUESTION_GENERATION" -> contextBuilder.append("You are an educational assessment expert. ");
        }

        // Add educational context
        addEducationalContext(contextBuilder, request);

        // Add minimal session context for actions
        if (session.getCurrentSubject() != null) {
            contextBuilder.append("Subject Context: ").append(session.getCurrentSubject()).append("\n");
        }

        return contextBuilder.toString();
    }

    /**
     * Add educational context from request
     */
    private void addEducationalContext(StringBuilder contextBuilder, ChatRequest request) {
        if (request.getEducationalContext() != null && !request.getEducationalContext().isEmpty()) {
            contextBuilder.append("Educational Context:\n");

            Map<String, Object> eduContext = request.getEducationalContext();

            if (eduContext.containsKey("subject")) {
                contextBuilder.append("- Subject: ").append(eduContext.get("subject")).append("\n");
            }
            if (eduContext.containsKey("gradeLevel")) {
                contextBuilder.append("- Grade Level: ").append(eduContext.get("gradeLevel")).append("\n");
            }
            if (eduContext.containsKey("lessonTopic")) {
                contextBuilder.append("- Lesson Topic: ").append(eduContext.get("lessonTopic")).append("\n");
            }
            if (eduContext.containsKey("duration")) {
                contextBuilder.append("- Duration: ").append(eduContext.get("duration")).append("\n");
            }
            if (eduContext.containsKey("classSize")) {
                contextBuilder.append("- Class Size: ").append(eduContext.get("classSize")).append("\n");
            }

            contextBuilder.append("\n");
        }
    }

    /**
     * Add session-specific context
     */
    private void addSessionContext(StringBuilder contextBuilder, ChatSessionEntity session) {
        contextBuilder.append("Session Context:\n");

        if (session.getCurrentSubject() != null) {
            contextBuilder.append("- Current Subject: ").append(session.getCurrentSubject()).append("\n");
        }
        if (session.getEducationLevel() != null) {
            contextBuilder.append("- Education Level: ").append(session.getEducationLevel()).append("\n");
        }
        if (session.getPreferredLanguage() != null) {
            contextBuilder.append("- Preferred Language: ").append(session.getPreferredLanguage()).append("\n");
        }

        contextBuilder.append("\n");
    }

    /**
     * Add recent conversation history for context
     */
    private void addConversationHistory(StringBuilder contextBuilder, ChatSessionEntity session) {
        List<ChatMessageEntity> recentMessages = messagePersistenceService
            .getRecentMessages(session.getSessionId(), 6); // Last 3 exchanges

        if (!recentMessages.isEmpty()) {
            contextBuilder.append("Recent Conversation:\n");

            for (ChatMessageEntity msg : recentMessages) {
                String sender = msg.getSender() == ChatMessageEntity.MessageSender.USER ? "Educator" : "Assistant";
                String content = msg.getContent();

                // Truncate long messages for context
                if (content.length() > 150) {
                    content = content.substring(0, 147) + "...";
                }

                contextBuilder.append("- ").append(sender).append(": ").append(content).append("\n");
            }

            contextBuilder.append("\n");
        }
    }

    /**
     * Estimate the token count for context (simple estimation)
     */
    public int estimateContextTokens(String context) {
        // Simple estimation: roughly 4 characters per token
        return context.length() / 4;
    }
}
