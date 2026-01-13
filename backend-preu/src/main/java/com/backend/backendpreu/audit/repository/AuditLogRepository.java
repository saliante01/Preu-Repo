package com.backend.backendpreu.audit.repository;

import com.backend.backendpreu.audit.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Consulta JPQL que maneja filtros opcionales
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