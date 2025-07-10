package com.educatorassistantchat.fullstackproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Chat Session Entity for database persistence - Designed for Educators
 */
@Entity
@Table(name = "chat_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSessionEntity {

    @Id
    @Column(name = "session_id", length = 50)
    private String sessionId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "title", length = 200)
    private String title;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "current_subject", length = 100)
    private String currentSubject;

    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage;

    @Column(name = "education_level", length = 50)
    private String educationLevel;

    @Column(name = "topics", columnDefinition = "TEXT")
    private String topics; // JSON array of topics

    @Column(name = "last_action_type", length = 30)
    private String lastActionType;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessageEntity> messages;
}
