package com.nomolestar.notificationservice.controller;

import com.nomolestar.notificationservice.dto.NotificationResponseDTO;
import com.nomolestar.notificationservice.service.NotificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE')")
    public List<NotificationResponseDTO> findAll() {
        return notificationService.findAll();
    }

    @GetMapping("/unread")
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE')")
    public List<NotificationResponseDTO> findUnread() {
        return notificationService.findUnread();
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE')")
    public NotificationResponseDTO markAsRead(@PathVariable Integer id) {
        return notificationService.markAsRead(id);
    }
}
