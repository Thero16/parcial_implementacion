package com.nomolestar.caseservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomolestar.caseservice.controller.CaseController;
import com.nomolestar.caseservice.dto.CaseCreateDTO;
import com.nomolestar.caseservice.dto.CaseResponseDTO;
import com.nomolestar.caseservice.dto.CaseUpdateDTO;
import com.nomolestar.caseservice.enums.CasePriority;
import com.nomolestar.caseservice.enums.InvestigationStatus;
import com.nomolestar.caseservice.exceptions.GlobalExceptionHandler;
import com.nomolestar.caseservice.exceptions.ResourceNotFoundException;
import com.nomolestar.caseservice.service.CaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CaseController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class CaseControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private CaseService caseService;

    private final CaseResponseDTO sampleResponse = new CaseResponseDTO(
            1, "Test Case", "Description",
            InvestigationStatus.OPEN, CasePriority.HIGH,
            "Detective Smith", LocalDateTime.now()
    );

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_returns200() throws Exception {
        when(caseService.findAll()).thenReturn(List.of(sampleResponse));
        mockMvc.perform(get("/cases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Case"));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void getAll_viewerRole_returns200() throws Exception {
        when(caseService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/cases"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_found_returns200() throws Exception {
        when(caseService.findById(1)).thenReturn(sampleResponse);
        mockMvc.perform(get("/cases/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_notFound_returns404() throws Exception {
        when(caseService.findById(99)).thenThrow(new ResourceNotFoundException("Case not found"));
        mockMvc.perform(get("/cases/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_validBody_returns201() throws Exception {
        CaseCreateDTO dto = new CaseCreateDTO(
                "New Case", "Desc", InvestigationStatus.OPEN,
                CasePriority.MEDIUM, "Detective Jones", LocalDateTime.now());
        when(caseService.create(any())).thenReturn(sampleResponse);
        mockMvc.perform(post("/cases")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_found_returns200() throws Exception {
        CaseUpdateDTO dto = new CaseUpdateDTO(
                "Updated", "Desc",
                InvestigationStatus.IN_PROGRESS, CasePriority.HIGH, "Det Smith");
        when(caseService.updateById(eq(1), any())).thenReturn(sampleResponse);
        mockMvc.perform(put("/cases/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_notFound_returns404() throws Exception {
        CaseUpdateDTO dto = new CaseUpdateDTO(
                "Updated", "Desc",
                InvestigationStatus.IN_PROGRESS, CasePriority.HIGH, "Det Smith");
        when(caseService.updateById(eq(99), any()))
                .thenThrow(new ResourceNotFoundException("Case not found"));
        mockMvc.perform(put("/cases/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_found_returns204() throws Exception {
        doNothing().when(caseService).deleteById(1);
        mockMvc.perform(delete("/cases/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Case not found")).when(caseService).deleteById(99);
        mockMvc.perform(delete("/cases/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
