package com.the.dailytasks.сontroller;


import com.the.dailytasks.model.Task;
import com.the.dailytasks.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public List<Task> getTasks(@RequestParam LocalDate start, @RequestParam LocalDate end, @RequestParam(required = false) Boolean completed) {
        return taskService.getTasksForPeriod(start, end, completed);
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @PutMapping("/{id}")
    public Optional<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }

    @PatchMapping("/{id}/toggle")
    public boolean toggleTask(@PathVariable Long id) {
        return taskService.toggleCompletion(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
