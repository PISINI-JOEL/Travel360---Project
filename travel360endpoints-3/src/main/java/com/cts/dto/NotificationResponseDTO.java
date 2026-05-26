package com.cts.dto;

import java.time.LocalDateTime;

import com.cts.enums.NotificationCategory;
import com.cts.enums.NotificationStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponseDTO {

    private Long notificationId;
    private String message;
    private NotificationCategory category;
    private NotificationStatus status;
    private LocalDateTime createdDate;
}
