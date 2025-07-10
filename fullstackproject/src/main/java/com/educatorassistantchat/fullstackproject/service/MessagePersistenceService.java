package com.educatorassistantchat.fullstackproject.service;

import com.educatorassistantchat.fullstackproject.dto.ChatRequest;
import com.educatorassistantchat.fullstackproject.dto.ChatSession;
import com.educatorassistantchat.fullstackproject.model.ChatMessageEntity;
import com.educatorassistantchat.fullstackproject.model.ChatSessionEntity;
import com.educatorassistantchat.fullstackproject.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service responsible for message storage and retrieval operations
 * Handles saving user messages, AI responses, and chat history management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessagePersistenceService {

    private final ChatMessageRepository messageRepository;

    /**
     * Save user message to database
     */
    @Transactional
    public ChatMessageEntity saveUserMessage(ChatRequest request, ChatSessionEntity session) {
        String messageId = generateMessageId();

        ChatMessageEntity userMessage = ChatMessageEntity.builder()
            .messageId(messageId)
            .content(request.getPrompt())
            .sender(ChatMessageEntity.MessageSender.USER)
            .session(session)
            .actionType(request.getActionType())
            .build();

        ChatMessageEntity savedMessage = messageRepository.save(userMessage);
        log.debug("Saved user message {} for session {}", messageId, session.getSessionId());
        return savedMessage;
    }

    /**
     * Save AI response message to database
     */
    @Transactional
    public ChatMessageEntity saveAIMessage(String response, ChatRequest request,
                                          ChatSessionEntity session, String aiModel, int tokensUsed) {
        String messageId = generateMessageId();

        ChatMessageEntity aiMessage = ChatMessageEntity.builder()
            .messageId(messageId)
            .content(response)
            .sender(ChatMessageEntity.MessageSender.ASSISTANT)
            .session(session)
            .actionType(request.getActionType())
            .aiModel(aiModel)
            .tokensUsed(tokensUsed)
            .build();

        ChatMessageEntity savedMessage = messageRepository.save(aiMessage);
        log.debug("Saved AI message {} for session {} using model {}",
                 messageId, session.getSessionId(), aiModel);
        return savedMessage;
    }

    /**
     * Get chat history for a session
     */
    public ChatSession getChatHistory(String sessionId, String userId) {
        List<ChatMessageEntity> messages = messageRepository
            .findBySession_SessionIdOrderByTimestampAsc(sessionId);

        if (messages.isEmpty()) {
            throw new RuntimeException("No messages found for session: " + sessionId);
        }

        // Get session info from first message
        ChatSessionEntity session = messages.get(0).getSession();

        // Verify user access
        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied to session: " + sessionId);
        }

        return ChatSession.builder()
            .sessionId(session.getSessionId())
            .userId(session.getUserId())
            .title(session.getTitle())
            .createdAt(session.getCreatedAt())
            .lastActivity(session.getLastActivity())
            .isActive(session.getIsActive())
            .messages(messages.stream().map(this::convertToMessageDTO).collect(Collectors.toList()))
            .build();
    }

    /**
     * Get recent messages for context building
     */
    public List<ChatMessageEntity> getRecentMessages(String sessionId, int limit) {
        long totalMessages = messageRepository.countBySession_SessionId(sessionId);

        if (totalMessages <= limit) {
            return messageRepository.findBySession_SessionIdOrderByTimestampAsc(sessionId);
        }

        return messageRepository.findBySession_SessionIdOrderByTimestampAsc(sessionId)
            .stream()
            .skip(Math.max(0, totalMessages - limit))
            .collect(Collectors.toList());
    }

    /**
     * Search messages by content
     */
    public List<ChatMessageEntity> searchMessages(String userId, String searchTerm, int maxResults) {
        Pageable pageable = PageRequest.of(0, maxResults);
        return messageRepository.searchMessagesByContent(searchTerm, pageable)
            .stream()
            .filter(msg -> msg.getSession().getUserId().equals(userId))
            .collect(Collectors.toList());
    }

    /**
     * Get message count for a session
     */
    public long getMessageCount(String sessionId) {
        return messageRepository.countBySession_SessionId(sessionId);
    }

    /**
     * Get recent messages by user (across all sessions)
     */
    public List<ChatMessageEntity> getRecentMessagesByUser(String userId, int maxResults) {
        Pageable pageable = PageRequest.of(0, maxResults);
        return messageRepository.findRecentMessagesByUserId(userId, pageable);
    }

    /**
     * Generate unique message ID
     */
    private String generateMessageId() {
        return "msg_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Convert message entity to DTO
     */
    private ChatSession.ChatMessage convertToMessageDTO(ChatMessageEntity entity) {
        return ChatSession.ChatMessage.builder()
            .messageId(entity.getMessageId())
            .content(entity.getContent())
            .sender(entity.getSender().name())
            .timestamp(entity.getTimestamp())
            .actionType(entity.getActionType())
            .build();
    }
}
