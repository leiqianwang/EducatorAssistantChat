package com.educatorassistantchat.fullstackproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * Chat Request DTO for handling incoming chat messages with flexible action parameters
 *
 * Example usage with standardized parameters:
 * - Regular chat: { prompt: "Help me create a lesson plan", actionType: null, actionParams: null }
 * - Translation: { prompt: "Hello students", actionType: "TRANSLATE", actionParams: {"targetLanguage": "German", "originalLanguage": "English", "tone": "formal"} }
 * - Summarization: { prompt: "Long content...", actionType: "SUMMARIZE", actionParams: {"summaryType": "BULLET_POINTS", "maxLength": 200} }
 * - Question Generation: { prompt: "Chapter content...", actionType: "QUESTION_GENERATION", actionParams: {"questionCount": 5, "difficultyLevel": "intermediate", "questionTypes": ["multiple_choice", "short_answer"]} }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    @NotBlank(message = "Prompt content cannot be empty")
    @Size(max = 4000, message = "Prompt content cannot exceed 4000 characters")
    private String prompt;

    private String sessionId;

    private String userId;

    private String actionType; // TRANSLATE, SUMMARIZE, REWRITE, QUESTION_GENERATION

    /**
     * Standardized parameters for different action types (validated against ActionType enum)
     *
     * TRANSLATE params:
     * - "targetLanguage": "Spanish", "German", "French", etc. (REQUIRED)
     * - "originalLanguage": "English" (optional, auto-detect if not provided)
     * - "tone": "formal", "informal", "academic", "neutral"
     *
     * SUMMARIZE params:
     * - "summaryType": "PARAGRAPH", "BULLET_POINTS", "KEY_CONCEPTS"
     * - "maxLength": integer (word count limit)
     * - "focusArea": "main_ideas", "actionable_items", "key_facts"
     *
     * REWRITE params:
     * - "targetAudience": "elementary", "middle_school", "high_school", "college"
     * - "tone": "professional", "conversational", "academic", "simple"
     * - "purpose": "lesson_plan", "student_handout", "parent_communication"
     *
     * QUESTION_GENERATION params:
     * - "questionCount": integer (number of questions to generate)
     * - "difficultyLevel": "beginner", "intermediate", "advanced"
     * - "questionTypes": ["multiple_choice", "short_answer", "essay", "true_false"]
     * - "cognitiveLevel": ["knowledge", "comprehension", "application", "analysis"]
     */
    private Map<String, Object> actionParams;

    /**
     * Additional educational context for better AI responses
     *
     * Example context params:
     * - "subject": "Mathematics", "Science", "English", "History"
     * - "gradeLevel": "K-2", "3-5", "6-8", "9-12"
     * - "lessonTopic": "Fractions", "World War II", "Photosynthesis"
     * - "classSize": integer
     * - "duration": "30 minutes", "1 hour"
     */
    private Map<String, Object> educationalContext;
}
