package com.educatorassistantchat.fullstackproject.service;

import com.educatorassistantchat.fullstackproject.dto.ChatRequest;
import com.educatorassistantchat.fullstackproject.model.ChatSessionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Specialized service for educational question generation
 * Creates pedagogically sound questions based on content and educational context
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionGenerationService {

    private final com.educatorassistantchat.fullstackproject.config.ChatAssistantConfig.OllamaService ollamaService;

    private final ActionParameterService actionParameterService;
    private final ContextBuilderService contextBuilderService;

    public String processQuestionGeneration(ChatRequest request, ChatSessionEntity session) {
        log.info("Processing question generation for session: {}", session.getSessionId());

        Map<String, Object> params = actionParameterService.getMergedParams(request.getActionType(), request.getActionParams());

        Integer questionCount = (Integer) params.get("questionCount");
        String difficultyLevel = (String) params.get("difficultyLevel");

        @SuppressWarnings("unchecked")
        List<String> questionTypes = (List<String>) params.get("questionTypes");

        @SuppressWarnings("unchecked")
        List<String> cognitiveLevel = (List<String>) params.get("cognitiveLevel");

        String educationalContext = buildEducationalContext(request, session);

        String promptText = """
            You are an expert educational assessment designer with deep knowledge of pedagogy and learning objectives.
            
            {educationalContext}
            
            Question Generation Requirements:
            - Generate {questionCount} educational questions
            - Difficulty Level: {difficultyLevel}
            - Question Types: {questionTypes}
            - Cognitive Levels: {cognitiveLevel}
            
            Guidelines:
            - Align questions with learning objectives
            - Use Bloom's Taxonomy for cognitive levels
            - Provide clear, unambiguous questions
            - Include answer keys or rubrics where appropriate
            - Ensure questions are age-appropriate
            - Mix different question types for comprehensive assessment
            
            Content for question generation: {content}
            
            Format your response with:
            1. Question number and type
            2. The actual question
            3. Answer options (for multiple choice)
            4. Correct answer or key points (for other types)
            """;

        // Replace placeholders in the prompt
        String finalPrompt = promptText
                .replace("{educationalContext}", educationalContext)
                .replace("{questionCount}", questionCount.toString())
                .replace("{difficultyLevel}", difficultyLevel)
                .replace("{questionTypes}", String.join(", ", questionTypes))
                .replace("{cognitiveLevel}", String.join(", ", cognitiveLevel))
                .replace("{content}", request.getPrompt());

        String result = ollamaService.generateResponse(finalPrompt).block();
        log.debug("Generated {} questions for {} difficulty level", questionCount, difficultyLevel);

        return result;
    }

    private String buildEducationalContext(ChatRequest request, ChatSessionEntity session) {
        Map<String, Object> enhancedContext = contextBuilderService
            .buildEnhancedEducationalContext(request, session);

        StringBuilder context = new StringBuilder();
        context.append("Educational Assessment Context:\n");

        if (enhancedContext.containsKey("subject")) {
            context.append("- Subject: ").append(enhancedContext.get("subject")).append("\n");
        }
        if (enhancedContext.containsKey("gradeLevel")) {
            context.append("- Grade Level: ").append(enhancedContext.get("gradeLevel")).append("\n");
        }
        if (enhancedContext.containsKey("lessonTopic")) {
            context.append("- Lesson Topic: ").append(enhancedContext.get("lessonTopic")).append("\n");
        }
        if (enhancedContext.containsKey("duration")) {
            context.append("- Assessment Duration: ").append(enhancedContext.get("duration")).append("\n");
        }

        return context.toString();
    }
}
