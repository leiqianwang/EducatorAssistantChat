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
 * Specialized service for content rewriting with educational focus
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RewritingService {

    @Qualifier("rewritingChatClient")
    private final ChatClient rewritingChatClient;

    private final ActionParameterService actionParameterService;
    private final ContextBuilderService contextBuilderService;

    public String processRewriting(ChatRequest request, ChatSessionEntity session) {
        log.info("Processing rewriting for session: {}", session.getSessionId());

        Map<String, Object> params = actionParameterService.getMergedParams(request.getActionType(), request.getActionParams());

        String targetAudience = (String) params.get("targetAudience");
        String tone = (String) params.get("tone");
        String purpose = (String) params.get("purpose");

        String educationalContext = buildEducationalContext(request, session);

        String promptText = """
            You are an expert educational content editor and writer.
            
            {educationalContext}
            
            Rewriting Requirements:
            - Target Audience: {targetAudience}
            - Tone: {tone}
            - Purpose: {purpose}
            - Maintain educational value and accuracy
            - Improve clarity and engagement
            - Use age-appropriate vocabulary
            - Preserve key learning objectives
            
            Content to rewrite: {content}
            """;

        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Prompt prompt = promptTemplate.create(Map.of(
            "educationalContext", educationalContext,
            "targetAudience", targetAudience.replace("_", " "),
            "tone", tone,
            "purpose", purpose.replace("_", " "),
            "content", request.getPrompt()
        ));

        return rewritingChatClient.prompt(prompt).call().content();
    }

    private String buildEducationalContext(ChatRequest request, ChatSessionEntity session) {
        Map<String, Object> enhancedContext = contextBuilderService
            .buildEnhancedEducationalContext(request, session);

        StringBuilder context = new StringBuilder();
        if (!enhancedContext.isEmpty()) {
            context.append("Educational Context:\n");
            enhancedContext.forEach((key, value) ->
                context.append("- ").append(key).append(": ").append(value).append("\n"));
        }
        return context.toString();
    }
}
