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
 * Specialized service for content summarization with educational focus
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SummarizationService {

    @Qualifier("summarizationChatClient")
    private final ChatClient summarizationChatClient;

    private final ActionParameterService actionParameterService;
    private final ContextBuilderService contextBuilderService;

    public String processSummarization(ChatRequest request, ChatSessionEntity session) {
        log.info("Processing summarization for session: {}", session.getSessionId());

        Map<String, Object> params = actionParameterService.getMergedParams(request.getActionType(), request.getActionParams());

        String summaryType = (String) params.get("summaryType");
        Integer maxLength = (Integer) params.get("maxLength");
        String focusArea = (String) params.get("focusArea");

        String educationalContext = buildEducationalContext(request, session);

        String promptText = """
            You are an expert educational content summarizer.
            
            {educationalContext}
            
            Summarization Requirements:
            - Create a {summaryType} summary
            - Focus on {focusArea}
            - Limit to approximately {maxLength} words
            - Make it suitable for educational purposes
            - Highlight key learning objectives if present
            - Use clear, accessible language
            
            Content to summarize: {content}
            """;

        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Prompt prompt = promptTemplate.create(Map.of(
            "educationalContext", educationalContext,
            "summaryType", summaryType.toLowerCase().replace("_", " "),
            "focusArea", focusArea.replace("_", " "),
            "maxLength", maxLength.toString(),
            "content", request.getPrompt()
        ));

        return summarizationChatClient.prompt(prompt).call().content();
    }

    private String buildEducationalContext(ChatRequest request, ChatSessionEntity session) {
        Map<String, Object> enhancedContext = contextBuilderService
            .buildEnhancedEducationalContext(request, session);

        StringBuilder context = new StringBuilder();
        if (!enhancedContext.isEmpty()) {
            context.append("Educational Context: Subject - ")
                   .append(enhancedContext.getOrDefault("subject", "General"))
                   .append(", Grade Level - ")
                   .append(enhancedContext.getOrDefault("gradeLevel", "General"))
                   .append("\n");
        }
        return context.toString();
    }
}
