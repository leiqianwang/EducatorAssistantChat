package com.educatorassistantchat.fullstackproject.model;

import lombok.Getter;
import lombok.AllArgsConstructor;

import java.util.*;

/**
 * Enum for all supported AI action types with their specific parameter schemas
 */
@Getter
@AllArgsConstructor
public enum ActionType {

    TRANSLATE("Translation Service", Set.of(
        "targetLanguage",     // Required: Target language for translation
        "originalLanguage",   // Optional: Source language (auto-detect if not provided)
        "tone"               // Optional: formal, informal, academic, neutral
    )),

    SUMMARIZE("Content Summarization", Set.of(
        "summaryType",       // Optional: PARAGRAPH, BULLET_POINTS, KEY_CONCEPTS
        "maxLength",         // Optional: Word count limit (integer)
        "focusArea"         // Optional: main_ideas, actionable_items, key_facts
    )),

    REWRITE("Content Rewriting", Set.of(
        "targetAudience",    // Optional: elementary, middle_school, high_school, college
        "tone",             // Optional: professional, conversational, academic, simple
        "purpose"           // Optional: lesson_plan, student_handout, parent_communication
    )),

    QUESTION_GENERATION("Educational Question Generation", Set.of(
        "questionCount",     // Optional: Number of questions (integer, default: 5)
        "difficultyLevel",   // Optional: beginner, intermediate, advanced
        "questionTypes",     // Optional: List<String> [multiple_choice, short_answer, essay, true_false]
        "cognitiveLevel"     // Optional: List<String> [knowledge, comprehension, application, analysis]
    ));

    private final String description;
    private final Set<String> supportedParams;

    /**
     * Validate if the provided parameters are supported for this action type
     */
    public boolean isValidParam(String paramName) {
        return supportedParams.contains(paramName);
    }

    /**
     * Get all supported parameters for this action type
     */
    public Set<String> getSupportedParams() {
        return new HashSet<>(supportedParams);
    }

    /**
     * Validate action parameters against the schema (basic structure validation only)
     */
    public List<String> validateParams(Map<String, Object> actionParams) {
        List<String> errors = new ArrayList<>();

        if (actionParams == null) {
            return errors; // No params is valid
        }

        // Only check for unsupported parameters - delegate detailed validation to ActionParameterService
        for (String paramName : actionParams.keySet()) {
            if (!isValidParam(paramName)) {
                errors.add("Unsupported parameter '" + paramName + "' for action type " + this.name());
            }
        }

        return errors;
    }

    /**
     * Get default parameters for this action type
     */
    public Map<String, Object> getDefaultParams() {
        // This method will be replaced by ActionParameterService using ActionDefaultsProperties
        // Keeping minimal fallback defaults here
        Map<String, Object> defaults = new HashMap<>();

        switch (this) {
            case TRANSLATE -> {
                defaults.put("targetLanguage", "Spanish");
                defaults.put("originalLanguage", "auto-detect");
                defaults.put("tone", "neutral");
            }
            case SUMMARIZE -> {
                defaults.put("summaryType", "PARAGRAPH");
                defaults.put("maxLength", 200);
                defaults.put("focusArea", "main_ideas");
            }
            case REWRITE -> {
                defaults.put("targetAudience", "general");
                defaults.put("tone", "professional");
                defaults.put("purpose", "educational");
            }
            case QUESTION_GENERATION -> {
                defaults.put("questionCount", 5);
                defaults.put("difficultyLevel", "intermediate");
                defaults.put("questionTypes", List.of("multiple_choice", "short_answer"));
                defaults.put("cognitiveLevel", List.of("knowledge", "comprehension", "application"));
            }
        }

        return defaults;
    }

    /**
     * Check if this action type requires educational context
     */
    public boolean requiresEducationalContext() {
        return this == QUESTION_GENERATION || this == REWRITE;
    }

    /**
     * Get recommended educational context fields for this action type
     */
    public Set<String> getRecommendedContextFields() {
        return switch (this) {
            case QUESTION_GENERATION -> Set.of("subject", "gradeLevel", "lessonTopic", "duration");
            case REWRITE -> Set.of("gradeLevel", "subject");
            case SUMMARIZE -> Set.of("subject", "gradeLevel");
            default -> Set.of();
        };
    }
}
