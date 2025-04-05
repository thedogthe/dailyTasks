package com.the.dailytasks;

import com.the.dailytasks.controller.TaskController;
import com.the.dailytasks.model.Task;
import com.the.dailytasks.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTasks_ShouldReturnPageOfTasks() {
        // Arrange
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 31);
        Boolean completed = false;
        Pageable pageable = PageRequest.of(0, 10);
        List<Task> tasks = Arrays.asList(
                new Task(1L, "Task 1", "Description 1", false, LocalDate.now()),
                new Task(2L, "Task 2", "Description 2", false, LocalDate.now())
        );
        Page<Task> taskPage = new PageImpl<>(tasks, pageable, tasks.size());

        when(taskService.getTasks(start, end, completed, pageable)).thenReturn(taskPage);

        // Act
        ResponseEntity<Page<Task>> response = taskController.getAllTasks(start, end, completed, pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getTotalElements());
        verify(taskService, times(1)).getTasks(start, end, completed, pageable);
    }

    @Test
    void getTodayTasks_ShouldReturnTodayTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(
                new Task(1L, "Today Task 1", "Description", false, LocalDate.now()),
                new Task(2L, "Today Task 2", "Description", true, LocalDate.now())
        );
        when(taskService.getTodayTasks(true)).thenReturn(tasks);

        // Act
        ResponseEntity<List<Task>> response = taskController.getTodayTasks(true);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(taskService, times(1)).getTodayTasks(true);
    }

    @Test
    void getTaskById_ShouldReturnTask() {
        // Arrange
        Long taskId = 1L;
        Task task = new Task(taskId, "Test Task", "Description", false, LocalDate.now());
        when(taskService.getTaskById(taskId)).thenReturn(task);

        // Act
        ResponseEntity<Task> response = taskController.getTaskById(taskId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskId, response.getBody().getId());
        verify(taskService, times(1)).getTaskById(taskId);
    }

    @Test
    void createTask_ShouldReturnCreatedTask() {
        // Arrange
        Task newTask = new Task(null, "New Task", "Description", false, LocalDate.now().plusDays(1));
        Task savedTask = new Task(1L, "New Task", "Description", false, LocalDate.now().plusDays(1));
        when(taskService.createTask(newTask)).thenReturn(savedTask);

        // Act
        ResponseEntity<Task> response = taskController.createTask(newTask);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        verify(taskService, times(1)).createTask(newTask);
    }

    @Test
    void updateTask_ShouldReturnUpdatedTask() {
        // Arrange
        Long taskId = 1L;
        Task existingTask = new Task(taskId, "Old Title", "Old Desc", false, LocalDate.now());
        Task updatedTask = new Task(taskId, "New Title", "New Desc", true, LocalDate.now().plusDays(1));

        when(taskService.updateTask(taskId, updatedTask)).thenReturn(updatedTask);

        // Act
        ResponseEntity<Task> response = taskController.updateTask(taskId, updatedTask);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New Title", response.getBody().getTitle());
        assertEquals("New Desc", response.getBody().getDescription());
        verify(taskService, times(1)).updateTask(taskId, updatedTask);
    }

    @Test
    void toggleTaskCompletion_ShouldToggleCompletionStatus() {
        // Arrange
        Long taskId = 1L;
        Task task = new Task(taskId, "Task", "Desc", false, LocalDate.now());
        Task toggledTask = new Task(taskId, "Task", "Desc", true, LocalDate.now());

        when(taskService.toggleCompletion(taskId)).thenReturn(toggledTask);

        // Act
        ResponseEntity<Task> response = taskController.toggleTaskCompletion(taskId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isCompleted());
        verify(taskService, times(1)).toggleCompletion(taskId);
    }




    @Test
    void deleteTask_ShouldReturnNoContent() {
        // Arrange
        Long taskId = 1L;
        doNothing().when(taskService).deleteTask(taskId);

        // Act
        ResponseEntity<Void> response = taskController.deleteTask(taskId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskService, times(1)).deleteTask(taskId);
    }
}