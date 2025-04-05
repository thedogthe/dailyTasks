package com.the.dailytasks.service;

import com.the.dailytasks.exception.TaskNotFoundException;
import com.the.dailytasks.model.Task;
import com.the.dailytasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для работы с задачами (Task).
 * Предоставляет бизнес-логику для операций с задачами, включая создание, чтение,
 * обновление и удаление (CRUD), а также дополнительные методы для работы с задачами.
 * Все методы чтения выполняются в режиме read-only транзакции.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * Получает задачи с возможностью фильтрации по датам и статусу выполнения с пагинацией.
     *
     * @param start начальная дата диапазона (может быть null)
     * @param end конечная дата диапазона (может быть null)
     * @param completed статус выполнения задачи (может быть null)
     * @param pageable параметры пагинации
     * @return страница с задачами согласно заданным фильтрам
     */
    public Page<Task> getTasks(LocalDate start, LocalDate end, Boolean completed, Pageable pageable) {
        if (start != null && end != null && completed != null) {
            return taskRepository.findByDueDateBetweenAndCompleted(start, end, completed, pageable);
        } else if (start != null && end != null) {
            return taskRepository.findByDueDateBetween(start, end, pageable);
        } else if (completed != null) {
            return taskRepository.findByCompleted(completed, pageable);
        }
        return taskRepository.findAll(pageable);
    }

    /**
     * Получает задачи на текущий день.
     *
     * @param includeCompleted включать ли выполненные задачи
     * @return список задач на сегодня
     */
    public List<Task> getTodayTasks(boolean includeCompleted) {
        LocalDate today = LocalDate.now();
        return includeCompleted
                ? taskRepository.findByDueDate(today)
                : taskRepository.findByDueDateAndCompleted(today, false);
    }

    /**
     * Получает задачи на текущую неделю (7 дней от текущей даты).
     *
     * @param includeCompleted включать ли выполненные задачи
     * @return список задач на неделю
     */
    public List<Task> getWeekTasks(boolean includeCompleted) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusWeeks(1);
        return includeCompleted
                ? taskRepository.findByDueDateBetween(start, end)
                : taskRepository.findByDueDateBetweenAndCompleted(start, end, false);
    }

    /**
     * Получает задачи на текущий месяц (30 дней от текущей даты).
     *
     * @param includeCompleted включать ли выполненные задачи
     * @return список задач на месяц
     */
    public List<Task> getMonthTasks(boolean includeCompleted) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(1);
        return includeCompleted
                ? taskRepository.findByDueDateBetween(start, end)
                : taskRepository.findByDueDateBetweenAndCompleted(start, end, false);
    }

    /**
     * Получает задачу по идентификатору.
     *
     * @param id идентификатор задачи
     * @return найденная задача
     * @throws TaskNotFoundException если задача не найдена
     */
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    /**
     * Ищет задачи по частичному совпадению названия.
     *
     * @param title часть названия для поиска
     * @param includeCompleted включать ли выполненные задачи
     * @return список найденных задач
     */
    public List<Task> searchTasksByTitle(String title, boolean includeCompleted) {
        return includeCompleted ?
                taskRepository.findByTitleContaining(title) :
                taskRepository.findByTitleContainingAndCompleted(title, false);
    }

    /**
     * Создает новую задачу.
     *
     * @param task данные новой задачи
     * @return созданная задача
     * @throws IllegalArgumentException если дата выполнения в прошлом
     */
    @Transactional
    public Task createTask(Task task) {
        validateTaskDates(task);
        return taskRepository.save(task);
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param id идентификатор задачи для обновления
     * @param newTask новые данные задачи
     * @return обновленная задача
     * @throws TaskNotFoundException если задача не найдена
     * @throws IllegalArgumentException если дата выполнения в прошлом
     */
    @Transactional
    public Task updateTask(Long id, Task newTask) {
        validateTaskDates(newTask);

        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(newTask.getTitle());
                    existingTask.setDescription(newTask.getDescription());
                    existingTask.setDueDate(newTask.getDueDate());
                    existingTask.setCompleted(newTask.isCompleted());
                    return taskRepository.save(existingTask);
                })
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    /**
     * Переключает статус выполнения задачи (выполнена/не выполнена).
     *
     * @param id идентификатор задачи
     * @return обновленная задача
     * @throws TaskNotFoundException если задача не найдена
     */
    @Transactional
    public Task toggleCompletion(Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setCompleted(!task.isCompleted());
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    /**
     * Устанавливает статус задачи "не выполнена".
     *
     * @param id идентификатор задачи
     * @return обновленная задача
     * @throws TaskNotFoundException если задача не найдена
     */
    @Transactional
    public Task toggleUnCompletion(Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setCompleted(false);
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    /**
     * Удаляет задачу по идентификатору.
     *
     * @param id идентификатор задачи
     * @throws TaskNotFoundException если задача не найдена
     */
    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    /**
     * Проверяет корректность дат задачи.
     *
     * @param task задача для проверки
     * @throws IllegalArgumentException если дата выполнения в прошлом
     */
    private void validateTaskDates(Task task) {
        if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
    }
}