package com.educatorassistantchat.fullstackproject.service;

import com.educatorassistantchat.fullstackproject.config.ActionDefaultsProperties;
import com.educatorassistantchat.fullstackproject.dto.ChatRequest;
import com.educatorassistantchat.fullstackproject.model.ActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for validating and processing action parameters using configurable defaults
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActionParameterService {

    private final ActionDefaultsProperties actionDefaults;

    /**
     * Validate chat request action parameters and structure
     */
    public List<String> validateRequest(ChatRequest request) {
        List<String> errors = new ArrayList<>();

        if (request.getActionType() == null) {
            return errors; // No validation needed for regular chat
        }

        try {
            ActionType actionType = ActionType.valueOf(request.getActionType().toUpperCase());

            // Validate action parameters structure and types
            List<String> paramErrors = actionType.validateParams(request.getActionParams());
            errors.addAll(paramErrors);

            // Validate educational context if required
            if (actionType.requiresEducationalContext() &&
                (request.getEducationalContext() == null || request.getEducationalContext().isEmpty())) {
                log.warn("Educational context recommended for action type: {}", actionType);
                // Don't add as error, just log warning
            }

        } catch (IllegalArgumentException e) {
            errors.add("Invalid action type: " + request.getActionType());
        }

        return errors;
    }

    /**
     * Validate action parameters for a specific action type (alternative/simplified approach)
     */
    public List<String> validateActionParams(ActionType actionType, Map<String, Object> params) {
        List<String> errors = new ArrayList<>();

        if (params == null) {
            return errors; // No params is valid
        }

        // Check for unsupported parameters
        for (String paramName : params.keySet()) {
            if (!actionType.isValidParam(paramName)) {
                errors.add("Unsupported parameter '" + paramName + "' for action type " + actionType.name());
            }
        }

        // Add basic type validations (simplified version)
        switch (actionType) {
            case TRANSLATE -> {
                if (params.containsKey("targetLanguage") && params.get("targetLanguage") == null) {
                    errors.add("targetLanguage cannot be null");
                }
            }
            case QUESTION_GENERATION -> {
                if (params.containsKey("questionCount")) {
                    Object count = params.get("questionCount");
                    if (!(count instanceof Integer) || (Integer) count <= 0) {
                        errors.add("questionCount must be a positive integer");
                    }
                }
            }
            case SUMMARIZE -> {
                if (params.containsKey("maxLength")) {
                    Object length = params.get("maxLength");
                    if (!(length instanceof Integer) || (Integer) length <= 0) {
                        errors.add("maxLength must be a positive integer");
                    }
                }
            }
        }

        return errors;
    }

    /**
     * Validate prompt content against action type requirements (separate concern)
     */
    public List<String> validatePromptContent(String prompt, ActionType actionType) {
        List<String> warnings = new ArrayList<>();

        if (prompt == null || prompt.trim().isEmpty()) {
            warnings.add("Prompt content is empty");
            return warnings;
        }

        // Content-specific validations based on action type
        switch (actionType) {
            case TRANSLATE -> {
                if (prompt.length() > 2000) {
                    warnings.add("Translation text is quite long, consider breaking into smaller segments");
                }
            }
            case QUESTION_GENERATION -> {
                if (prompt.length() < 50) {
                    warnings.add("Content seems too short for meaningful question generation");
                }
            }
            case SUMMARIZE -> {
                if (prompt.length() < 100) {
                    warnings.add("Content might be too short to summarize effectively");
                }
            }
        }

        return warnings;
    }

    /**
     * Get merged parameters with configurable defaults
     */
    public Map<String, Object> getMergedParams(String actionTypeStr, Map<String, Object> userParams) {
        if (actionTypeStr == null) {
            return new HashMap<>();
        }

        try {
            ActionType actionType = ActionType.valueOf(actionTypeStr.toUpperCase());

            // Get defaults from configuration (preferred) or fallback to enum defaults
            Map<String, Object> mergedParams = getConfigurableDefaults(actionType);

            if (userParams != null) {
                mergedParams.putAll(userParams);
            }

            return mergedParams;
        } catch (IllegalArgumentException e) {
            log.error("Invalid action type: {}", actionTypeStr);
            return userParams != null ? userParams : new HashMap<>();
        }
    }

    /**
     * Get configurable default parameters from properties
     */
    private Map<String, Object> getConfigurableDefaults(ActionType actionType) {
        return switch (actionType) {
            case TRANSLATE -> actionDefaults.getTranslate().toMap();
            case SUMMARIZE -> actionDefaults.getSummarize().toMap();
            case REWRITE -> actionDefaults.getRewrite().toMap();
            case QUESTION_GENERATION -> actionDefaults.getQuestionGeneration().toMap();
        };
    }

    /**
     * Get action type enum from string
     */
    public Optional<ActionType> getActionType(String actionTypeStr) {
        if (actionTypeStr == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(ActionType.valueOf(actionTypeStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Build enhanced educational context from session and request
     */
    public Map<String, Object> buildEnhancedContext(ChatRequest request, String sessionSubject) {
        Map<String, Object> enhancedContext = new HashMap<>();

        // Add request context
        if (request.getEducationalContext() != null) {
            enhancedContext.putAll(request.getEducationalContext());
        }

        // Add session context if not already present
        if (sessionSubject != null && !enhancedContext.containsKey("subject")) {
            enhancedContext.put("subject", sessionSubject);
        }

        return enhancedContext;
    }
}
