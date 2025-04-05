package com.the.dailytasks.сontroller;

import com.the.dailytasks.model.Task;
import com.the.dailytasks.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    // Получение задач с фильтрацией и пагинацией
    @GetMapping
    public ResponseEntity<Page<Task>> getAllTasks(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) Boolean completed,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasks(start, end, completed, pageable));
    }

    // Получение задач на сегодня
    @GetMapping("/today")
    public ResponseEntity<List<Task>> getTodayTasks(
            @RequestParam(defaultValue = "false") boolean includeCompleted) {
        return ResponseEntity.ok(taskService.getTodayTasks(includeCompleted));
    }

    // Получение задач на неделю
    @GetMapping("/week")
    public ResponseEntity<List<Task>> getWeekTasks(
            @RequestParam(defaultValue = "false") boolean includeCompleted) {
        return ResponseEntity.ok(taskService.getWeekTasks(includeCompleted));
    }

    // Получение задач на месяц
    @GetMapping("/month")
    public ResponseEntity<List<Task>> getMonthTasks(
            @RequestParam(defaultValue = "false") boolean includeCompleted) {
        return ResponseEntity.ok(taskService.getMonthTasks(includeCompleted));
    }

    // Получение конкретной задачи
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    // Создание новой задачи
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(task));
    }

    // Полное обновление задачи
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        return ResponseEntity.ok(taskService.updateTask(id, task));
    }

    // Частичное обновление (только статус выполнения)
    @PatchMapping("/{id}/completion")
    public ResponseEntity<Task> toggleTaskCompletion(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.toggleCompletion(id));
    }

    // Удаление задачи
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // Поиск задач по названию
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(
            @RequestParam String title,
            @RequestParam(defaultValue = "false") boolean exactMatch) {
        return ResponseEntity.ok(taskService.searchTasksByTitle(title, exactMatch));
    }
}