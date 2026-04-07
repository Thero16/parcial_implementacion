package com.nomolestar.peopleservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomolestar.peopleservice.controller.PersonController;
import com.nomolestar.peopleservice.dto.PersonCreateDTO;
import com.nomolestar.peopleservice.dto.PersonResponseDTO;
import com.nomolestar.peopleservice.dto.PersonUpdateDTO;
import com.nomolestar.peopleservice.enums.PersonRole;
import com.nomolestar.peopleservice.exceptions.GlobalExceptionHandler;
import com.nomolestar.peopleservice.exceptions.ResourceNotFoundException;
import com.nomolestar.peopleservice.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class PersonControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private PersonService personService;

    private final PersonResponseDTO sampleResponse = new PersonResponseDTO(
            1, 10, "John Doe", PersonRole.DETECTIVE, 35, "Test person"
    );

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_returns200() throws Exception {
        when(personService.findAll()).thenReturn(List.of(sampleResponse));
        mockMvc.perform(get("/people"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("John Doe"));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void getAll_viewerRole_returns200() throws Exception {
        when(personService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/people"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_found_returns200() throws Exception {
        when(personService.findById(1)).thenReturn(sampleResponse);
        mockMvc.perform(get("/people/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_notFound_returns404() throws Exception {
        when(personService.findById(99)).thenThrow(new ResourceNotFoundException("Person not found"));
        mockMvc.perform(get("/people/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByCaseId_returns200() throws Exception {
        when(personService.findByCaseId(10)).thenReturn(List.of(sampleResponse));
        mockMvc.perform(get("/people/case/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].caseId").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_validBody_returns201() throws Exception {
        PersonCreateDTO dto = new PersonCreateDTO(10, "John Doe", PersonRole.DETECTIVE, 35, "Desc");
        when(personService.create(any(), any())).thenReturn(sampleResponse);
        mockMvc.perform(post("/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_found_returns200() throws Exception {
        PersonUpdateDTO dto = new PersonUpdateDTO("Jane Doe", PersonRole.SUSPECT, 28, "Updated");
        when(personService.updateById(eq(1), any())).thenReturn(sampleResponse);
        mockMvc.perform(put("/people/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_notFound_returns404() throws Exception {
        PersonUpdateDTO dto = new PersonUpdateDTO("Jane Doe", PersonRole.SUSPECT, 28, "Updated");
        when(personService.updateById(eq(99), any()))
                .thenThrow(new ResourceNotFoundException("Person not found"));
        mockMvc.perform(put("/people/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_found_returns204() throws Exception {
        doNothing().when(personService).deleteById(1);
        mockMvc.perform(delete("/people/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Person not found")).when(personService).deleteById(99);
        mockMvc.perform(delete("/people/99"))
                .andExpect(status().isNotFound());
    }
}
