package com.educatorassistantchat.fullstackproject.service;

import com.educatorassistantchat.fullstackproject.dto.ChatRequest;
import com.educatorassistantchat.fullstackproject.dto.ChatResponse;
import com.educatorassistantchat.fullstackproject.model.ActionType;
import com.educatorassistantchat.fullstackproject.model.ChatMessageEntity;
import com.educatorassistantchat.fullstackproject.model.ChatSessionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Main orchestration service that coordinates chat request processing
 * Acts as the central coordinator between specialized services
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatOrchestrationService {

    @Qualifier("generalChatClient")
    private final ChatClient generalChatClient;

    private final SessionManagementService sessionManagementService;
    private final MessagePersistenceService messagePersistenceService;
    private final ContextBuilderService contextBuilderService;
    private final ActionParameterService actionParameterService;
    private final SuggestedPromptsService suggestedPromptsService;

    // Action-specific services (will be injected)
    private final TranslationService translationService;
    private final SummarizationService summarizationService;
    private final RewritingService rewritingService;
    private final QuestionGenerationService questionGenerationService;

    /**
     * Main entry point for processing chat requests
     */
    @Transactional
    public ChatResponse processChat(ChatRequest request) {
        try {
            log.info("Processing chat request for educator user: {}", request.getUserId());

            // Step 1: Validate request parameters
            List<String> validationErrors = validateRequest(request);
            if (!validationErrors.isEmpty()) {
                return buildErrorResponse("Parameter validation failed: " + String.join(", ", validationErrors));
            }

            // Step 2: Get or create session
            ChatSessionEntity session = sessionManagementService.getOrCreateSession(request);

            // Step 3: Save user message
            ChatMessageEntity userMessage = messagePersistenceService.saveUserMessage(request, session);

            // Step 4: Generate AI response
            String aiResponse = generateAIResponse(request, session);

            // Step 5: Save AI response
            String aiModel = determineAIModel(request);
            int tokensUsed = estimateTokens(aiResponse);
            ChatMessageEntity aiMessage = messagePersistenceService.saveAIMessage(
                aiResponse, request, session, aiModel, tokensUsed);

            // Step 6: Generate suggested prompts
            List<String> suggestedPrompts = suggestedPromptsService
                .generateContextualPrompts(session.getCurrentSubject(), request.getActionType());

            // Step 7: Update session activity
            sessionManagementService.updateSessionActivity(session, request);

            // Step 8: Build and return response
            return buildSuccessResponse(aiMessage, session, suggestedPrompts, aiModel, tokensUsed, request);

        } catch (Exception e) {
            log.error("Error processing chat request for user {}: ", request.getUserId(), e);
            return buildErrorResponse("An error occurred while processing your request: " + e.getMessage());
        }
    }

    /**
     * Validate incoming chat request
     */
    private List<String> validateRequest(ChatRequest request) {
        List<String> errors = actionParameterService.validateRequest(request);

        // Additional orchestration-level validations
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            errors.add("User ID is required");
        }

        if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
            errors.add("Prompt content is required");
        }

        return errors;
    }

    /**
     * Generate AI response by routing to appropriate service
     */
    private String generateAIResponse(ChatRequest request, ChatSessionEntity session) {
        if (request.getActionType() != null && !request.getActionType().trim().isEmpty()) {
            return handleActionBasedRequest(request, session);
        } else {
            return handleRegularChatRequest(request, session);
        }
    }

    /**
     * Handle action-specific requests by routing to specialized services
     */
    private String handleActionBasedRequest(ChatRequest request, ChatSessionEntity session) {
        String actionType = request.getActionType().toUpperCase();

        return switch (actionType) {
            case "TRANSLATE" -> translationService.processTranslation(request, session);
            case "SUMMARIZE" -> summarizationService.processSummarization(request, session);
            case "REWRITE" -> rewritingService.processRewriting(request, session);
            case "QUESTION_GENERATION" -> questionGenerationService.processQuestionGeneration(request, session);
            default -> {
                log.warn("Unknown action type: {}, falling back to general chat", actionType);
                yield handleRegularChatRequest(request, session);
            }
        };
    }

    /**
     * Handle regular chat requests (no specific action)
     */
    private String handleRegularChatRequest(ChatRequest request, ChatSessionEntity session) {
        String contextualMessage = contextBuilderService.buildContextualMessage(request, session);

        return generalChatClient.prompt()
            .user(contextualMessage)
            .call()
            .content();
    }

    /**
     * Build successful response
     */
    private ChatResponse buildSuccessResponse(ChatMessageEntity aiMessage, ChatSessionEntity session,
                                            List<String> suggestedPrompts, String aiModel,
                                            int tokensUsed, ChatRequest request) {
        return ChatResponse.builder()
            .messageId(aiMessage.getMessageId())
            .content(aiMessage.getContent())
            .sessionId(session.getSessionId())
            .timestamp(LocalDateTime.now())
            .status("SUCCESS")
            .aiModel(aiModel)
            .suggestedPrompts(suggestedPrompts)
            .tokensUsed(tokensUsed)
            .actionMetadata(buildActionMetadata(request))
            .build();
    }

    /**
     * Build error response
     */
    private ChatResponse buildErrorResponse(String errorMessage) {
        return ChatResponse.builder()
            .status("ERROR")
            .errorMessage(errorMessage)
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Build action metadata for response
     */
    private ChatResponse.ActionMetadata buildActionMetadata(ChatRequest request) {
        if (request.getActionType() == null) return null;

        String targetLanguage = null;
        if (request.getActionParams() != null && request.getActionParams().containsKey("targetLanguage")) {
            targetLanguage = (String) request.getActionParams().get("targetLanguage");
        }

        return ChatResponse.ActionMetadata.builder()
            .actionType(request.getActionType())
            .targetLanguage(targetLanguage)
            .build();
    }

    /**
     * Determine which AI model to use based on request
     */
    private String determineAIModel(ChatRequest request) {
        if (request.getActionType() != null) {
            // Use more powerful model for complex actions
            return switch (request.getActionType().toUpperCase()) {
                case "QUESTION_GENERATION", "REWRITE" -> "gpt-4";
                default -> "gpt-3.5-turbo";
            };
        }
        return "gpt-3.5-turbo";
    }

    /**
     * Simple token estimation
     */
    private int estimateTokens(String text) {
        return contextBuilderService.estimateContextTokens(text);
    }
}
