package com.the.dailytasks.repository;

import com.the.dailytasks.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Основные методы с пагинацией
    Page<Task> findByDueDateBetween(LocalDate start, LocalDate end, Pageable pageable);
    Page<Task> findByCompleted(boolean completed, Pageable pageable);
    Page<Task> findByDueDateBetweenAndCompleted(LocalDate start, LocalDate end, boolean completed, Pageable pageable);

    // Методы без пагинации для удобства
    List<Task> findByDueDate(LocalDate dueDate);
    List<Task> findByDueDateAndCompleted(LocalDate dueDate, boolean completed);
    List<Task> findByDueDateBetween(LocalDate start, LocalDate end);
    List<Task> findByDueDateBetweenAndCompleted(LocalDate start, LocalDate end, boolean completed);

    // Методы для стандартных периодов с возможностью фильтрации по статусу
    default List<Task> findTodayTasks(boolean includeCompleted) {
        LocalDate today = LocalDate.now();
        return includeCompleted ?
                findByDueDate(today) :
                findByDueDateAndCompleted(today, false);
    }

    default List<Task> findWeekTasks(boolean includeCompleted) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusWeeks(1);
        return includeCompleted ?
                findByDueDateBetween(start, end) :
                findByDueDateBetweenAndCompleted(start, end, false);
    }

    default List<Task> findMonthTasks(boolean includeCompleted) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(1);
        return includeCompleted ?
                findByDueDateBetween(start, end) :
                findByDueDateBetweenAndCompleted(start, end, false);
    }

    // Оптимизированные запросы с сортировкой
    @Query("SELECT t FROM Task t WHERE t.dueDate = :date ORDER BY t.dueDate ASC")
    List<Task> findTasksByDateWithSorting(@Param("date") LocalDate date);

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :start AND :end ORDER BY t.dueDate ASC")
    List<Task> findTasksByPeriodWithSorting(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :start AND :end AND t.completed = :completed ORDER BY t.dueDate ASC")
    List<Task> findTasksByPeriodAndStatusWithSorting(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("completed") boolean completed);

    List<Task> findByTitleContaining(String title);

    List<Task> findByTitleContainingAndCompleted(String title, boolean b);
}