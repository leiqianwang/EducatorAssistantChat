package com.educatorassistantchat.fullstackproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Chat Message Entity for database persistence
 */
@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", unique = true, nullable = false, length = 50)
    private String messageId;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender", nullable = false)
    private MessageSender sender;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "action_type", length = 30)
    private String actionType;

    @Column(name = "action_metadata", columnDefinition = "TEXT")
    private String actionMetadata; // JSON metadata for actions

    @Column(name = "ai_model", length = 50)
    private String aiModel;

    @Column(name = "tokens_used")
    private Integer tokensUsed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id")
    private ChatSessionEntity session;

    public enum MessageSender {
        USER, ASSISTANT
    }
}
