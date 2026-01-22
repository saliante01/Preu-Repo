package com.backend.backendpreu.audit.repository;

import com.backend.backendpreu.audit.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link AuditLog} entities.
 * Provides methods for querying and filtering audit logs.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Searches for audit logs based on optional filters for user ID, action, and entity name.
     * Results are ordered by timestamp in descending order.
     *
     * @param userId The ID of the user (actor) who performed the action. Can be {@code null} to ignore this filter.
     * @param action The specific action performed (e.g., "CREATE_USER"). Can be {@code null} to ignore this filter.
     * @param entityName The name of the entity affected (e.g., "USER"). Can be {@code null} to ignore this filter.
     * @return A list of {@link AuditLog} entities matching the criteria.
     */
    @Query("SELECT a FROM AuditLog a " +
            "WHERE (:userId IS NULL OR a.user.id = :userId) " +
            "AND (:action IS NULL OR a.action = :action) " +
            "AND (:entityName IS NULL OR a.entityName = :entityName) " +
            "ORDER BY a.timestamp DESC")
    List<AuditLog> searchAuditLogs(
            @Param("userId") Long userId,
            @Param("action") String action,
            @Param("entityName") String entityName
    );
}
