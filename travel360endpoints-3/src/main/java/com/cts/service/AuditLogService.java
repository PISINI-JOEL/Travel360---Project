package com.cts.service;

import java.util.List;

import com.cts.dto.AuditLogResponseDTO;
import com.cts.entity.AuditLog;
import com.cts.entity.User;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;

public interface AuditLogService {

    void logAction(String action,
                   AuditEntity entityType,
                   Long entityId,
                   User user,
                   LogType logType);

    List<AuditLogResponseDTO> getAllLogs();

    List<AuditLogResponseDTO> getByEntity(AuditEntity entityType, Long entityId);

    List<AuditLogResponseDTO> getByUser(Long userId);

    List<AuditLogResponseDTO> getByAction(String action);

    List<AuditLogResponseDTO> getByLogType(LogType logType);
}