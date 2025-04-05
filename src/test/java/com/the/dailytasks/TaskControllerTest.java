package com.the.dailytasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.the.dailytasks.model.Task;
import com.the.dailytasks.service.TaskService;
import com.the.dailytasks.controller.TaskController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task testTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setDueDate(LocalDate.now());
        testTask.setCompleted(false);
    }

    @Test
    void testGetAllTasks() throws Exception {
        List<Task> tasks = Collections.emptyList();
        Page<Task> taskPage = new PageImpl<>(tasks);

        when(taskService.getTasks(null, null, null, null)).thenReturn(taskPage);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(taskService, times(1)).getTasks(null, null, null, null);
    }
    @Test
    void testGetTodayTasks() throws Exception {
        when(taskService.getTodayTasks(false)).thenReturn(List.of(testTask));

        mockMvc.perform(get("/tasks/today"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Test Task"));

        verify(taskService, times(1)).getTodayTasks(false);
    }

    @Test
    void testGetWeekTasks() throws Exception {
        when(taskService.getWeekTasks(false)).thenReturn(List.of(testTask));

        mockMvc.perform(get("/tasks/week"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Test Task"));

        verify(taskService, times(1)).getWeekTasks(false);
    }

    @Test
    void testGetMonthTasks() throws Exception {
        when(taskService.getMonthTasks(false)).thenReturn(List.of(testTask));

        mockMvc.perform(get("/tasks/month"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Test Task"));

        verify(taskService, times(1)).getMonthTasks(false);
    }

    @Test
    void testGetTaskById() throws Exception {
        when(taskService.getTaskById(anyLong())).thenReturn(testTask);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    void testCreateTask() throws Exception {
        when(taskService.createTask(any(Task.class))).thenReturn(testTask);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void testUpdateTask() throws Exception {
        when(taskService.updateTask(anyLong(), any(Task.class))).thenReturn(testTask);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(taskService, times(1)).updateTask(1L, any(Task.class));
    }

    @Test
    void testToggleCompletion() throws Exception {
        when(taskService.toggleCompletion(anyLong())).thenReturn(testTask);

        mockMvc.perform(patch("/tasks/1/completion"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(taskService, times(1)).toggleCompletion(1L);
    }

    @Test
    void testDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask(anyLong());

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }

    @Test
    void testSearchTasks() throws Exception {
        when(taskService.searchTasksByTitle(any(String.class), any(Boolean.class))).thenReturn(List.of(testTask));

        mockMvc.perform(get("/tasks/search")
                        .param("title", "Test")
                        .param("exactMatch", "false"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Test Task"));

        verify(taskService, times(1)).searchTasksByTitle("Test", false);
    }
}