package com.educatorassistantchat.fullstackproject.controller;

import com.educatorassistantchat.fullstackproject.dto.ChatRequest;
import com.educatorassistantchat.fullstackproject.dto.ChatResponse;
import com.educatorassistantchat.fullstackproject.dto.ChatSession;
import com.educatorassistantchat.fullstackproject.service.ChatService;
import com.educatorassistantchat.fullstackproject.service.SuggestedPromptsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for AI Chat functionality
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final SuggestedPromptsService suggestedPromptsService;

    /**
     * Send chat message and get AI response
     */
    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(@Valid @RequestBody ChatRequest request) {
        log.info("Received chat request from user: {}", request.getUserId());

        try {
            ChatResponse response = chatService.processChat(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing chat message: ", e);
            return ResponseEntity.internalServerError()
                .body(ChatResponse.builder()
                    .status("ERROR")
                    .errorMessage("An error occurred while processing your message")
                    .build());
        }
    }

    /**
     * Get chat history for a specific session
     */
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<ChatSession> getChatHistory(
            @PathVariable String sessionId,
            @RequestParam String userId) {

        try {
            ChatSession session = chatService.getChatHistory(sessionId, userId);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            log.error("Error retrieving chat history: ", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all sessions for a user
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSession>> getUserSessions(@RequestParam String userId) {
        try {
            List<ChatSession> sessions = chatService.getUserSessions(userId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            log.error("Error retrieving user sessions: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get suggested prompts based on subject and context
     */
    @GetMapping("/prompts")
    public ResponseEntity<List<String>> getSuggestedPrompts(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String actionType) {

        try {
            List<String> prompts = suggestedPromptsService
                .generateContextualPrompts(subject, actionType);
            return ResponseEntity.ok(prompts);
        } catch (Exception e) {
            log.error("Error generating suggested prompts: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Add custom prompt for educator
     */
    @PostMapping("/prompts/custom")
    public ResponseEntity<Map<String, String>> addCustomPrompt(
            @RequestParam String category,
            @RequestParam String prompt) {

        try {
            suggestedPromptsService.addCustomPrompt(category, prompt);
            return ResponseEntity.ok(Map.of("message", "Custom prompt added successfully"));
        } catch (Exception e) {
            log.error("Error adding custom prompt: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to add custom prompt"));
        }
    }

    /**
     * Perform specific AI action (translate, summarize, etc.)
     */
    @PostMapping("/action")
    public ResponseEntity<ChatResponse> performAction(@Valid @RequestBody ChatRequest request) {
        log.info("Received action request: {} from user: {}", request.getActionType(), request.getUserId());

        try {
            // Ensure action type is specified
            if (request.getActionType() == null || request.getActionType().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ChatResponse.builder()
                        .status("ERROR")
                        .errorMessage("Action type must be specified")
                        .build());
            }

            ChatResponse response = chatService.processChat(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error performing action: ", e);
            return ResponseEntity.internalServerError()
                .body(ChatResponse.builder()
                    .status("ERROR")
                    .errorMessage("An error occurred while performing the action")
                    .build());
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "healthy", "service", "chat-service"));
    }
}
