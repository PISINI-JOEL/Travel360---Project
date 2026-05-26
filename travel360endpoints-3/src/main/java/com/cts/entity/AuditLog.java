package com.cts.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "AuditLog")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    private String action;          

    private LocalDateTime timestamp;

    private String ipAddress;

    private String entityType;      

    private Long entityId;

   
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}