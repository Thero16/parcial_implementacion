package com.nomolestar.auditservice.integration;

import com.nomolestar.auditservice.controller.AuditLogController;
import com.nomolestar.auditservice.dto.AuditLogResponseDTO;
import com.nomolestar.auditservice.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.nomolestar.auditservice.config.SecurityConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuditLogController.class)
@Import(SecurityConfig.class)
class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private JwtDecoder jwtDecoder;

    private AuditLogResponseDTO sampleLog() {
        return new AuditLogResponseDTO(1, "case.created", "1", "Case created", LocalDateTime.now(), "system");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_returns200() throws Exception {
        when(auditLogService.findAll()).thenReturn(List.of(sampleLog()));

        mockMvc.perform(get("/audit-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("case.created"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_empty_returns200WithEmptyArray() throws Exception {
        when(auditLogService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/audit-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByEventType_returns200() throws Exception {
        when(auditLogService.findByEventType("case.created")).thenReturn(List.of(sampleLog()));

        mockMvc.perform(get("/audit-logs").param("eventType", "case.created"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("case.created"));
    }

    @Test
    @WithMockUser(roles = "DETECTIVE")
    void getAll_withDetectiveRole_returns403() throws Exception {
        mockMvc.perform(get("/audit-logs"))
                .andExpect(status().isForbidden());
    }
}
