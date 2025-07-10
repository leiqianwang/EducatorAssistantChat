package com.educatorassistantchat.fullstackproject.repository;

import com.educatorassistantchat.fullstackproject.model.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ChatSessionEntity - Educator focused
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, String> {

    /**
     * Find active sessions by user ID
     */
    List<ChatSessionEntity> findByUserIdAndIsActiveTrueOrderByLastActivityDesc(String userId);

    /**
     * Find session by session ID and user ID
     */
    Optional<ChatSessionEntity> findBySessionIdAndUserId(String sessionId, String userId);

    /**
     * Find recent sessions by user ID
     */
    @Query("SELECT s FROM ChatSessionEntity s WHERE s.userId = :userId " +
           "ORDER BY s.lastActivity DESC")
    List<ChatSessionEntity> findRecentSessionsByUserId(@Param("userId") String userId);

    /**
     * Find sessions created after a specific date
     */
    List<ChatSessionEntity> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime date);

    /**
     * Count active sessions by user ID
     */
    long countByUserIdAndIsActiveTrue(String userId);

    /**
     * Find sessions by subject
     */
    List<ChatSessionEntity> findByCurrentSubjectContainingIgnoreCaseAndIsActiveTrue(String subject);
}
