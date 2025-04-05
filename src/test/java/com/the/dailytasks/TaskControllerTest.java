package com.the.dailytasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.the.dailytasks.model.Task;
import com.the.dailytasks.service.TaskService;
import com.the.dailytasks.сontroller.TaskController; // Исправлено: 'сontroller' на 'controller'
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
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
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setDueDate(LocalDate.now());
        testTask.setCompleted(false);
    }

    @Test
    void getAllTasks_ShouldReturnPageOfTasks() throws Exception {
        Page<Task> page = new PageImpl<>(Collections.singletonList(testTask));
        Mockito.when(taskService.getTasks(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(page);

        mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Task"));
    }

    @Test
    void getTaskById_ShouldReturnTask() throws Exception {
        Mockito.when(taskService.getTaskById(1L)).thenReturn(testTask);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception {
        Mockito.when(taskService.createTask(Mockito.any(Task.class))).thenReturn(testTask);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void updateTask_ShouldReturnUpdatedTask() throws Exception {
        Mockito.when(taskService.updateTask(Mockito.eq(1L), Mockito.any(Task.class)))
                .thenReturn(testTask);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void toggleTaskCompletion_ShouldToggleStatus() throws Exception {
        Task completedTask = new Task();
        completedTask.setId(1L);
        completedTask.setCompleted(true);

        Mockito.when(taskService.toggleCompletion(1L)).thenReturn(completedTask);

        mockMvc.perform(patch("/tasks/1/completion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void deleteTask_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(taskService, Mockito.times(1)).deleteTask(1L);
    }
}