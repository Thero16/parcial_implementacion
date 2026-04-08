package com.nomolestar.evidenceservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomolestar.evidenceservice.controller.EvidenceController;
import com.nomolestar.evidenceservice.dto.EvidenceCreateDTO;
import com.nomolestar.evidenceservice.dto.EvidenceResponseDTO;
import com.nomolestar.evidenceservice.dto.EvidenceUpdateDTO;
import com.nomolestar.evidenceservice.enums.CustodyStatus;
import com.nomolestar.evidenceservice.enums.EvidenceType;
import com.nomolestar.evidenceservice.exceptions.GlobalExceptionHandler;
import com.nomolestar.evidenceservice.exceptions.ResourceNotFoundException;
import com.nomolestar.evidenceservice.service.EvidenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvidenceController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class EvidenceControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private EvidenceService evidenceService;

    private final EvidenceResponseDTO sampleResponse = new EvidenceResponseDTO(
            1, 10, EvidenceType.PHOTO, "A test photo", "Crime scene",
            LocalDateTime.now(), "Officer Jones", null,
            CustodyStatus.STORED, "Lab A"
    );

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_returns200() throws Exception {
        when(evidenceService.findAll()).thenReturn(List.of(sampleResponse));
        mockMvc.perform(get("/evidences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("A test photo"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_found_returns200() throws Exception {
        when(evidenceService.findById(1)).thenReturn(sampleResponse);
        mockMvc.perform(get("/evidences/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evidenceId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_notFound_returns404() throws Exception {
        when(evidenceService.findById(99))
                .thenThrow(new ResourceNotFoundException("Evidence not found"));
        mockMvc.perform(get("/evidences/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByCaseId_returns200() throws Exception {
        when(evidenceService.findByCaseId(10)).thenReturn(List.of(sampleResponse));
        mockMvc.perform(get("/evidences/case/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].caseId").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_validBody_returns201() throws Exception {
        EvidenceCreateDTO dto = new EvidenceCreateDTO(
                10, EvidenceType.PHOTO, "Description", "Location",
                LocalDateTime.now(), "Officer", null,
                CustodyStatus.COLLECTED, "Lab A");
        when(evidenceService.create(any(), any())).thenReturn(sampleResponse);
        mockMvc.perform(post("/evidences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_found_returns200() throws Exception {
        EvidenceUpdateDTO dto = new EvidenceUpdateDTO(
                EvidenceType.DOCUMENT, "Updated", "New Location",
                LocalDateTime.now(), "Officer New", null,
                CustodyStatus.IN_ANALYSIS, "Lab B", null);
        when(evidenceService.updateById(eq(1), any())).thenReturn(sampleResponse);
        mockMvc.perform(put("/evidences/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_notFound_returns404() throws Exception {
        EvidenceUpdateDTO dto = new EvidenceUpdateDTO(
                EvidenceType.PHOTO, "Desc", "Location",
                LocalDateTime.now(), "Officer", null,
                CustodyStatus.STORED, "Lab A", null);
        when(evidenceService.updateById(eq(99), any()))
                .thenThrow(new ResourceNotFoundException("Evidence not found"));
        mockMvc.perform(put("/evidences/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_found_returns204() throws Exception {
        doNothing().when(evidenceService).deleteById(1);
        mockMvc.perform(delete("/evidences/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Evidence not found"))
                .when(evidenceService).deleteById(99);
        mockMvc.perform(delete("/evidences/99"))
                .andExpect(status().isNotFound());
    }
}
