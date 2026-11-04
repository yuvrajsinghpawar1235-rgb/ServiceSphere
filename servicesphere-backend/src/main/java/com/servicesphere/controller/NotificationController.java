package com.servicesphere.controller;

import com.servicesphere.entity.Notification;
import com.servicesphere.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications(
            @RequestParam Long userId,
            @RequestParam Long roleId) {

        if (roleId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markNotificationRead(@PathVariable Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        return ResponseEntity.ok(notificationRepository.save(notification));
    }
}