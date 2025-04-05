package com.the.dailytasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.the.dailytasks.controller.TaskController;
import com.the.dailytasks.model.Task;
import com.the.dailytasks.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    void getAllTasks_ShouldReturnPaginatedTasks() throws Exception {
        // Arrange
        Task task1 = new Task(1L, "Task 1", "Description 1", false, LocalDate.now());
        Task task2 = new Task(2L, "Task 2", "Description 2", true, LocalDate.now().plusDays(1));
        List<Task> tasks = Arrays.asList(task1, task2);
        Page<Task> page = new PageImpl<>(tasks, PageRequest.of(0, 10), tasks.size());

        Mockito.when(taskService.getTasks(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.any(Pageable.class)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is("Task 1")))
                .andExpect(jsonPath("$.content[1].title", is("Task 2")))
                .andExpect(jsonPath("$.totalElements", is(2)));
    }

    @Test
    void getAllTasks_WithFilters_ShouldReturnFilteredTasks() throws Exception {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        Task task = new Task(1L, "Filtered Task", "Description", false, LocalDate.of(2023, 1, 15));
        Page<Task> page = new PageImpl<>(Collections.singletonList(task));

        Mockito.when(taskService.getTasks(Mockito.eq(startDate), Mockito.eq(endDate), Mockito.eq(false), Mockito.any(Pageable.class)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/tasks")
                        .param("start", "2023-01-01")
                        .param("end", "2023-01-31")
                        .param("completed", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title", is("Filtered Task")))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    void getTodayTasks_ShouldReturnTodayTasks() throws Exception {
        // Arrange
        Task task1 = new Task(1L, "Today Task 1", "Desc", false, LocalDate.now());
        Task task2 = new Task(2L, "Today Task 2", "Desc", true, LocalDate.now());
        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.getTodayTasks(true)).thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/tasks/today")
                        .param("includeCompleted", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Today Task 1")))
                .andExpect(jsonPath("$[1].title", is("Today Task 2")));
    }

    @Test
    void getTaskById_ShouldReturnTask() throws Exception {
        // Arrange
        Long taskId = 1L;
        Task task = new Task(taskId, "Single Task", "Description", false, LocalDate.now());

        Mockito.when(taskService.getTaskById(taskId)).thenReturn(task);

        // Act & Assert
        mockMvc.perform(get("/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskId.intValue())))
                .andExpect(jsonPath("$.title", is("Single Task")));
    }



    @Test
    void updateTask_ShouldReturnUpdatedTask() throws Exception {
        // Arrange
        Long taskId = 1L;
        Task updatedTask = new Task(taskId, "Updated Task", "New Description", true, LocalDate.now().plusDays(2));

        Mockito.when(taskService.updateTask(Mockito.eq(taskId), Mockito.any(Task.class))).thenReturn(updatedTask);

        // Act & Assert
        mockMvc.perform(put("/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Task")))
                .andExpect(jsonPath("$.description", is("New Description")))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void toggleTaskCompletion_ShouldToggleStatus() throws Exception {
        // Arrange
        Long taskId = 1L;
        Task toggledTask = new Task(taskId, "Task", "Desc", true, LocalDate.now());

        Mockito.when(taskService.toggleCompletion(taskId)).thenReturn(toggledTask);

        // Act & Assert
        mockMvc.perform(patch("/tasks/{id}/completion", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void deleteTask_ShouldReturnNoContent() throws Exception {
        // Arrange
        Long taskId = 1L;
        Mockito.doNothing().when(taskService).deleteTask(taskId);

        // Act & Assert
        mockMvc.perform(delete("/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Mockito.verify(taskService, Mockito.times(1)).deleteTask(taskId);
    }



    @Test
    void getWeekTasks_ShouldReturnWeekTasks() throws Exception {
        // Arrange
        Task task1 = new Task(1L, "Week Task 1", "Desc", false, LocalDate.now());
        Task task2 = new Task(2L, "Week Task 2", "Desc", true, LocalDate.now().plusDays(3));
        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.getWeekTasks(true)).thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/tasks/week")
                        .param("includeCompleted", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Week Task 1")))
                .andExpect(jsonPath("$[1].title", is("Week Task 2")));
    }

    @Test
    void getMonthTasks_ShouldReturnMonthTasks() throws Exception {
        // Arrange
        Task task1 = new Task(1L, "Month Task 1", "Desc", false, LocalDate.now());
        Task task2 = new Task(2L, "Month Task 2", "Desc", false, LocalDate.now().plusDays(15));
        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.getMonthTasks(false)).thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/tasks/month")
                        .param("includeCompleted", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Month Task 1")))
                .andExpect(jsonPath("$[1].title", is("Month Task 2")));
    }

    @Test
    void toggleTaskUnCompletion_ShouldSetUncompleted() throws Exception {
        // Arrange
        Long taskId = 1L;
        Task uncompletedTask = new Task(taskId, "Task", "Desc", false, LocalDate.now());

        Mockito.when(taskService.toggleUnCompletion(taskId)).thenReturn(uncompletedTask);

        // Act & Assert
        mockMvc.perform(patch("/tasks/{id}/uncompleted", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed", is(false)));
    }
}