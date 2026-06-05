package com.cts.serviceimpl;

import com.cts.dto.NotificationResponseDTO;
import com.cts.entity.Notification;
import com.cts.entity.User;
import com.cts.enums.NotificationCategory;
import com.cts.enums.NotificationStatus;
import com.cts.repository.NotificationRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository repo;

    @InjectMocks
    private NotificationServiceImpl service;

    // ✅ SEND NOTIFICATION
    @Test
    void sendNotification() {

        User user = new User();
        user.setUserId(1L);

        Notification saved = new Notification();
        saved.setNotificationId(1L);

        when(repo.save(any())).thenReturn(saved);

        service.sendNotification(user, "Test Message", NotificationCategory.PAYMENT);

        verify(repo).save(any());
    }

    // ✅ GET USER NOTIFICATIONS (NON-EMPTY)
    @Test
    void getUserNotifications_nonEmpty() {

        Notification n = new Notification();
        n.setNotificationId(1L);
        n.setMessage("Test");
        n.setCategory(NotificationCategory.PAYMENT);
        n.setStatus(NotificationStatus.UNREAD);
        n.setCreatedDate(LocalDateTime.now());

        when(repo.findByUserUserId(1L))
                .thenReturn(List.of(n));

        List<NotificationResponseDTO> result = service.getUserNotifications(1L);

        assertFalse(result.isEmpty());
        assertEquals("Test", result.get(0).getMessage());
    }

    // ✅ GET USER NOTIFICATIONS (EMPTY LIST)
    @Test
    void getUserNotifications_empty() {

        when(repo.findByUserUserId(1L))
                .thenReturn(List.of());

        List<NotificationResponseDTO> result = service.getUserNotifications(1L);

        assertTrue(result.isEmpty());
    }
}