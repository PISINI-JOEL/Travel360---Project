package com.cts.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.dto.NotificationResponseDTO;
import com.cts.service.NotificationService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

   
    @GetMapping("/{userId}")
    public ResponseEntity<?>  getUserNotifications(
            @PathVariable Long userId) {
    	List<NotificationResponseDTO> list1 = notificationService.getUserNotifications(userId);

        return new ResponseEntity<>(list1,HttpStatus.OK) ;
    }
}