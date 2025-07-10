package com.educatorassistantchat.fullstackproject.repository;

import com.educatorassistantchat.fullstackproject.model.ChatMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for ChatMessageEntity
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    /**
     * Find messages by session ID ordered by timestamp
     */
    List<ChatMessageEntity> findBySession_SessionIdOrderByTimestampAsc(String sessionId);

    /**
     * Find paginated messages by session ID
     */
    Page<ChatMessageEntity> findBySession_SessionIdOrderByTimestampDesc(String sessionId, Pageable pageable);

    /**
     * Find messages by action type
     */
    List<ChatMessageEntity> findByActionTypeOrderByTimestampDesc(String actionType);

    /**
     * Find recent messages by user
     */
    @Query("SELECT m FROM ChatMessageEntity m WHERE m.session.userId = :userId " +
           "ORDER BY m.timestamp DESC")
    List<ChatMessageEntity> findRecentMessagesByUserId(@Param("userId") String userId, Pageable pageable);

    /**
     * Count messages in session
     */
    long countBySession_SessionId(String sessionId);

    /**
     * Find messages by date range
     */
    List<ChatMessageEntity> findByTimestampBetweenOrderByTimestampDesc(
        LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Search messages by content
     */
    @Query("SELECT m FROM ChatMessageEntity m WHERE LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY m.timestamp DESC")
    List<ChatMessageEntity> searchMessagesByContent(@Param("searchTerm") String searchTerm, Pageable pageable);
}
