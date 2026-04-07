package com.nomolestar.workflowservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomolestar.workflowservice.controller.TaskController;
import com.nomolestar.workflowservice.dto.TaskCreateDTO;
import com.nomolestar.workflowservice.dto.TaskResponseDTO;
import com.nomolestar.workflowservice.dto.TaskUpdateDTO;
import com.nomolestar.workflowservice.enums.TaskPriority;
import com.nomolestar.workflowservice.enums.TaskStatus;
import com.nomolestar.workflowservice.exceptions.ResourceNotFoundException;
import com.nomolestar.workflowservice.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private TaskResponseDTO sampleResponse() {
        return new TaskResponseDTO(1, "Test Task", "Desc", 1,
                TaskStatus.PENDING, TaskPriority.HIGH, "detective_smith",
                null, LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_returns200() throws Exception {
        when(taskService.findAll()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_found_returns200() throws Exception {
        when(taskService.findById(1)).thenReturn(sampleResponse());

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_notFound_returns404() throws Exception {
        when(taskService.findById(99)).thenThrow(new ResourceNotFoundException("Task with id 99 not found"));

        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns201() throws Exception {
        TaskCreateDTO dto = new TaskCreateDTO("New Task", "Desc", 1, TaskPriority.HIGH, null, null);
        when(taskService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_found_returns200() throws Exception {
        TaskUpdateDTO dto = new TaskUpdateDTO("Updated", null, TaskStatus.IN_PROGRESS, null, null, null);
        when(taskService.updateById(eq(1), any())).thenReturn(sampleResponse());

        mockMvc.perform(put("/tasks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_notFound_returns404() throws Exception {
        TaskUpdateDTO dto = new TaskUpdateDTO("Updated", null, null, null, null, null);
        when(taskService.updateById(eq(99), any())).thenThrow(new ResourceNotFoundException("Task with id 99 not found"));

        mockMvc.perform(put("/tasks/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_found_returns204() throws Exception {
        mockMvc.perform(delete("/tasks/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Task with id 99 not found"))
                .when(taskService).deleteById(99);

        mockMvc.perform(delete("/tasks/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
