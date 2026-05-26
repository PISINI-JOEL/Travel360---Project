package com.cts.service;

import java.util.List;

import com.cts.dto.NotificationResponseDTO;
import com.cts.entity.Notification;
import com.cts.entity.User;
import com.cts.enums.NotificationCategory;

public interface NotificationService {
	


	void sendNotification(User user, String message, NotificationCategory category);

	List<NotificationResponseDTO> getUserNotifications(Long userId);


}
