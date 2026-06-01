package com.cts.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.dto.NotificationResponseDTO;
import com.cts.service.NotificationService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/notifications")
@AllArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserNotifications(@PathVariable Long userId) {

        log.info("Fetching notifications for userId: {}", userId);

        List<NotificationResponseDTO> list1 =
                notificationService.getUserNotifications(userId);

        log.info("Fetched {} notifications for userId: {}", list1.size(), userId);

        return new ResponseEntity<>(list1, HttpStatus.OK);
    }
}