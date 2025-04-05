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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    // Получение задач с пагинацией и фильтрацией
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

    // Удобные методы для стандартных периодов
    public List<Task> getTodayTasks(boolean includeCompleted) {
        LocalDate today = LocalDate.now();
        return includeCompleted
                ? taskRepository.findByDueDate(today)
                : taskRepository.findByDueDateAndCompleted(today, false);
    }

    public List<Task> getWeekTasks(boolean includeCompleted) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusWeeks(1);
        return includeCompleted
                ? taskRepository.findByDueDateBetween(start, end)
                : taskRepository.findByDueDateBetweenAndCompleted(start, end, false);
    }

    public List<Task> getMonthTasks(boolean includeCompleted) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(1);
        return includeCompleted
                ? taskRepository.findByDueDateBetween(start, end)
                : taskRepository.findByDueDateBetweenAndCompleted(start, end, false);
    }
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }
    public List<Task> searchTasksByTitle(String title, boolean includeCompleted) {
        return includeCompleted ?
                taskRepository.findByTitleContaining(title) :
                taskRepository.findByTitleContainingAndCompleted(title, false);
    }
    @Transactional
    public Task createTask(Task task) {
        validateTaskDates(task);
        return taskRepository.save(task);
    }

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

    @Transactional
    public Task toggleCompletion(Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setCompleted(!task.isCompleted());
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    private void validateTaskDates(Task task) {
        if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
    }
}