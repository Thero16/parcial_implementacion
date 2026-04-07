package com.nomolestar.notificationservice.integration;

import com.nomolestar.notificationservice.controller.NotificationController;
import com.nomolestar.notificationservice.dto.NotificationResponseDTO;
import com.nomolestar.notificationservice.enums.NotificationType;
import com.nomolestar.notificationservice.exceptions.ResourceNotFoundException;
import com.nomolestar.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    private NotificationResponseDTO sampleNotification() {
        return new NotificationResponseDTO(1, "New evidence added to case 1",
                NotificationType.EVIDENCE_ADDED, 1, false, LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_returns200() throws Exception {
        when(notificationService.findAll()).thenReturn(List.of(sampleNotification()));

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("New evidence added to case 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_empty_returns200() throws Exception {
        when(notificationService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "DETECTIVE")
    void getUnread_returns200() throws Exception {
        when(notificationService.findUnread()).thenReturn(List.of(sampleNotification()));

        mockMvc.perform(get("/notifications/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].read").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void markAsRead_found_returns200() throws Exception {
        NotificationResponseDTO readNotification = new NotificationResponseDTO(
                1, "New evidence added to case 1", NotificationType.EVIDENCE_ADDED, 1, true, LocalDateTime.now());
        when(notificationService.markAsRead(1)).thenReturn(readNotification);

        mockMvc.perform(put("/notifications/1/read").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void markAsRead_notFound_returns404() throws Exception {
        when(notificationService.markAsRead(99))
                .thenThrow(new ResourceNotFoundException("Notification with id 99 not found"));

        mockMvc.perform(put("/notifications/99/read").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void getAll_withViewerRole_returns403() throws Exception {
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isForbidden());
    }
}
