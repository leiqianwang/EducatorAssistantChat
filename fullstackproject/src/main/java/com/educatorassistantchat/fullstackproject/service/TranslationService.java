package com.educatorassistantchat.fullstackproject.service;

import com.educatorassistantchat.fullstackproject.dto.ChatRequest;
import com.educatorassistantchat.fullstackproject.model.ChatSessionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Specialized service for handling translation operations
 * Provides educational-focused translation with context awareness
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationService {

    @Qualifier("translationChatClient")
    private final ChatClient translationChatClient;

    private final ActionParameterService actionParameterService;
    private final ContextBuilderService contextBuilderService;

    /**
     * Process translation request with educational context
     */
    public String processTranslation(ChatRequest request, ChatSessionEntity session) {
        log.info("Processing translation request for session: {}", session.getSessionId());

        // Get merged parameters with defaults
        Map<String, Object> params = actionParameterService.getMergedParams(request.getActionType(), request.getActionParams());

        String targetLanguage = (String) params.get("targetLanguage");
        String originalLanguage = (String) params.get("originalLanguage");
        String tone = (String) params.get("tone");

        // Build educational context
        String educationalContext = buildEducationalContext(request, session);

        // Create translation prompt
        String promptText = """
            You are a professional educational translator specializing in classroom materials.
            
            {educationalContext}
            
            Translation Requirements:
            - Translate from {originalLanguage} to {targetLanguage}
            - Maintain a {tone} tone appropriate for educational settings
            - Preserve educational terminology and concepts
            - Ensure age-appropriate language for the target audience
            - Keep formatting and structure intact
            
            Text to translate: {content}
            
            Important: Provide only the translation without explanations unless specifically requested.
            """;

        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Prompt prompt = promptTemplate.create(Map.of(
            "educationalContext", educationalContext,
            "originalLanguage", originalLanguage,
            "targetLanguage", targetLanguage,
            "tone", tone,
            "content", request.getPrompt()
        ));

        String result = translationChatClient.prompt(prompt).call().content();
        log.debug("Translation completed: {} -> {} ({} characters)",
                 originalLanguage, targetLanguage, result.length());

        return result;
    }

    /**
     * Build educational context for translation
     */
    private String buildEducationalContext(ChatRequest request, ChatSessionEntity session) {
        StringBuilder context = new StringBuilder();

        Map<String, Object> enhancedContext = contextBuilderService
            .buildEnhancedEducationalContext(request, session);

        if (!enhancedContext.isEmpty()) {
            context.append("Educational Context:\n");

            if (enhancedContext.containsKey("subject")) {
                context.append("- Subject: ").append(enhancedContext.get("subject")).append("\n");
            }
            if (enhancedContext.containsKey("gradeLevel")) {
                context.append("- Grade Level: ").append(enhancedContext.get("gradeLevel")).append("\n");
            }
            if (enhancedContext.containsKey("lessonTopic")) {
                context.append("- Lesson Topic: ").append(enhancedContext.get("lessonTopic")).append("\n");
            }
        }

        return context.toString();
    }

    /**
     * Validate translation-specific parameters
     */
    public boolean validateTranslationParams(Map<String, Object> params) {
        if (params == null) return true;

        // Check if target language is provided
        if (!params.containsKey("targetLanguage") || params.get("targetLanguage") == null) {
            log.warn("Target language not specified for translation");
            return false;
        }

        return true;
    }
}
