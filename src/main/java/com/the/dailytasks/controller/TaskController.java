package com.the.dailytasks.controller;

import com.the.dailytasks.model.Task;
import com.the.dailytasks.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.time.LocalDate;
import java.util.List;

/**
 * Контроллер для работы с задачами через REST API.
 * Предоставляет эндпоинты для всех операций CRUD с задачами,
 * а также дополнительные методы для фильтрации и поиска задач.
 * Все методы возвращают данные в формате JSON.
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    /**
     * Получает список задач с возможностью фильтрации по датам и статусу выполнения.
     * Поддерживает пагинацию и сортировку.
     *
     * @param start начальная дата диапазона (необязательный параметр)
     * @param end конечная дата диапазона (необязательный параметр)
     * @param completed статус выполнения (необязательный параметр)
     * @param pageable параметры пагинации и сортировки
     * @return страница с задачами и статус OK
     */
    @GetMapping
    public ResponseEntity<Page<Task>> getAllTasks(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) Boolean completed,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasks(start, end, completed, pageable));
    }

    /**
     * Получает задачи на текущий день.
     *
     * @param includeCompleted включать ли выполненные задачи (по умолчанию false)
     * @return список задач и статус OK
     */
    @GetMapping("/today")
    public ResponseEntity<List<Task>> getTodayTasks(
            @RequestParam(defaultValue = "false") boolean includeCompleted) {
        return ResponseEntity.ok(taskService.getTodayTasks(includeCompleted));
    }

    /**
     * Получает задачи на текущую неделю.
     *
     * @param includeCompleted включать ли выполненные задачи (по умолчанию false)
     * @return список задач и статус OK
     */
    @GetMapping("/week")
    public ResponseEntity<List<Task>> getWeekTasks(
            @RequestParam(defaultValue = "false") boolean includeCompleted) {
        return ResponseEntity.ok(taskService.getWeekTasks(includeCompleted));
    }

    /**
     * Получает задачи на текущий месяц.
     *
     * @param includeCompleted включать ли выполненные задачи (по умолчанию false)
     * @return список задач и статус OK
     */
    @GetMapping("/month")
    public ResponseEntity<List<Task>> getMonthTasks(
            @RequestParam(defaultValue = "false") boolean includeCompleted) {
        return ResponseEntity.ok(taskService.getMonthTasks(includeCompleted));
    }

    /**
     * Получает задачу по идентификатору.
     *
     * @param id идентификатор задачи
     * @return найденная задача и статус OK
     * @throws com.the.dailytasks.exception.TaskNotFoundException если задача не найдена
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    /**
     * Создает новую задачу.
     *
     * @param task данные для создания задачи
     * @return созданная задача и статус CREATED
     * @throws IllegalArgumentException если дата выполнения некорректна
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(task));
    }

    /**
     * Полностью обновляет существующую задачу.
     *
     * @param id идентификатор задачи
     * @param task новые данные задачи
     * @return обновленная задача и статус OK
     * @throws com.the.dailytasks.exception.TaskNotFoundException если задача не найдена
     * @throws IllegalArgumentException если дата выполнения некорректна
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        return ResponseEntity.ok(taskService.updateTask(id, task));
    }

    /**
     * Переключает статус выполнения задачи (выполнена/не выполнена).
     *
     * @param id идентификатор задачи
     * @return обновленная задача и статус OK
     * @throws com.the.dailytasks.exception.TaskNotFoundException если задача не найдена
     */
    @PatchMapping("/{id}/completion")
    public ResponseEntity<Task> toggleTaskCompletion(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.toggleCompletion(id));
    }

    /**
     * Устанавливает статус задачи "не выполнена".
     *
     * @param id идентификатор задачи
     * @return обновленная задача и статус OK
     * @throws com.the.dailytasks.exception.TaskNotFoundException если задача не найдена
     */
    @PatchMapping("/{id}/uncompleted")
    public ResponseEntity<Task> toggleTaskUnCompletion(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.toggleUnCompletion(id));
    }

    /**
     * Удаляет задачу по идентификатору.
     *
     * @param id идентификатор задачи
     * @return статус NO_CONTENT
     * @throws com.the.dailytasks.exception.TaskNotFoundException если задача не найдена
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ищет задачи по частичному совпадению названия.
     *
     * @param title текст для поиска в названии
     * @param exactMatch точное совпадение (по умолчанию false)
     * @return список найденных задач и статус OK
     */
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(
            @RequestParam String title,
            @RequestParam(defaultValue = "false") boolean exactMatch) {
        return ResponseEntity.ok(taskService.searchTasksByTitle(title, exactMatch));
    }
}