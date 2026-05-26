package com.cts.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.dto.NotificationResponseDTO;
import com.cts.entity.Notification;
import com.cts.entity.User;
import com.cts.enums.NotificationCategory;
import com.cts.enums.NotificationStatus;
import com.cts.repository.NotificationRepository;
import com.cts.service.NotificationService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;

    
    @Override
    public void sendNotification(User user, String message, NotificationCategory category) {

        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .category(category)                        
                .status(NotificationStatus.UNREAD)         
                .createdDate(LocalDateTime.now())
                .build();

        notificationRepo.save(notification);
    }

    
    @Override
    public List<NotificationResponseDTO> getUserNotifications(Long userId) {

        return notificationRepo.findByUserUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    
    private NotificationResponseDTO mapToDTO(Notification n) {

        return NotificationResponseDTO.builder()
                .notificationId(n.getNotificationId())
                .message(n.getMessage())
                .category(n.getCategory())
                .status(n.getStatus())
                .createdDate(n.getCreatedDate())
                .build();
    }
}