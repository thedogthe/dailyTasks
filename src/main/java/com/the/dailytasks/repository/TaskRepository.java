package com.the.dailytasks.repository;


import com.the.dailytasks.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDueDateBetweenAndCompleted(LocalDate start, LocalDate end, boolean completed);
}
