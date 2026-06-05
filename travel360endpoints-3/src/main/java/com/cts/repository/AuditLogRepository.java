package com.cts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.AuditLog;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

	List<AuditLog> findByEntityTypeAndEntityId(AuditEntity entityType, Long entityId);

	List<AuditLog> findByUserUserId(Long userId);

	List<AuditLog> findByAction(String action);

	List<AuditLog> findByLogType(LogType logType);

}
