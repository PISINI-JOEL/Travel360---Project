package com.cts.serviceimpl;

import com.cts.dto.AuditLogResponseDTO;
import com.cts.entity.AuditLog;
import com.cts.entity.User;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.repository.AuditLogRepository;
import com.cts.service.AuditLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Async
    public void logAction(String action,
                          AuditEntity entityType,
                          Long entityId,
                          User user,
                          LogType logType) {

        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .user(user)
                .logType(logType)
                .build();

        auditLogRepository.save(auditLog);
    }

    @Override
    public List<AuditLogResponseDTO> getAllLogs() {
        return auditLogRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<AuditLogResponseDTO> getByEntity(AuditEntity entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }


    @Override
    public List<AuditLogResponseDTO> getByUser(Long userId) {
        return auditLogRepository.findByUserUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
    @Override
    public List<AuditLogResponseDTO> getByAction(String action) {
        return auditLogRepository.findByAction(action)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }



@Override
public List<AuditLogResponseDTO> getByLogType(LogType logType) {
    return auditLogRepository.findByLogType(logType)
            .stream()
            .map(this::mapToDTO)
            .toList();
}


    private AuditLogResponseDTO mapToDTO(AuditLog log) {
        User user = log.getUser();
        return AuditLogResponseDTO.builder()
                .auditId(log.getAuditId())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .logType(log.getLogType())
                .timestamp(log.getTimestamp())
                .userId(user != null ? user.getUserId() : null)
                .userEmail(user != null ? user.getEmail() : null)
                .build();
    }
    
}
