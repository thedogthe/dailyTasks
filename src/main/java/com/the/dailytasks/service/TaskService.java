package com.the.dailytasks.service;


import com.the.dailytasks.model.Task;
import com.the.dailytasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public List<Task> getTasksForPeriod(LocalDate start, LocalDate end, Boolean completed) {
        return taskRepository.findByDueDateBetweenAndCompleted(start, end, completed);
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Optional<Task> updateTask(Long id, Task newTask) {
        return taskRepository.findById(id).map(task -> {
            task.setTitle(newTask.getTitle());
            task.setDescription(newTask.getDescription());
            task.setDueDate(newTask.getDueDate());
            task.setCompleted(newTask.isCompleted());
            return taskRepository.save(task);
        });
    }

    public boolean toggleCompletion(Long id) {
        return taskRepository.findById(id).map(task -> {
            task.setCompleted(!task.isCompleted());
            taskRepository.save(task);
            return true;
        }).orElse(false);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
