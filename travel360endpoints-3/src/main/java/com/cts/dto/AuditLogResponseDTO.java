package com.cts.dto;

import java.time.LocalDateTime;

import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
@AllArgsConstructor
public class AuditLogResponseDTO {

    private Long auditId;
    private String action;
    private AuditEntity entityType;
    private Long entityId;
    private LogType logType;
    private LocalDateTime timestamp;

    private Long userId;
    private String userEmail;   // safe field
}