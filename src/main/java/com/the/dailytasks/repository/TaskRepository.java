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

/**
 * Репозиторий для работы с задачами (Task).
 * Предоставляет методы для поиска и фильтрации задач с поддержкой пагинации,
 * а также удобные методы для получения задач за стандартные периоды (сегодня, неделя, месяц).
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Находит задачи с датой выполнения в указанном диапазоне с поддержкой пагинации.
     *
     * @param start начальная дата диапазона (включительно)
     * @param end конечная дата диапазона (включительно)
     * @param pageable параметры пагинации
     * @return страница с задачами
     */
    Page<Task> findByDueDateBetween(LocalDate start, LocalDate end, Pageable pageable);

    /**
     * Находит задачи по статусу выполнения с поддержкой пагинации.
     *
     * @param completed статус выполнения задачи (true - выполнена, false - не выполнена)
     * @param pageable параметры пагинации
     * @return страница с задачами
     */
    Page<Task> findByCompleted(boolean completed, Pageable pageable);

    /**
     * Находит задачи с датой выполнения в указанном диапазоне и определенным статусом выполнения.
     *
     * @param start начальная дата диапазона (включительно)
     * @param end конечная дата диапазона (включительно)
     * @param completed статус выполнения задачи
     * @param pageable параметры пагинации
     * @return страница с задачами
     */
    Page<Task> findByDueDateBetweenAndCompleted(LocalDate start, LocalDate end, boolean completed, Pageable pageable);

    /**
     * Находит все задачи с указанной датой выполнения.
     *
     * @param dueDate дата выполнения задачи
     * @return список задач
     */
    List<Task> findByDueDate(LocalDate dueDate);

    /**
     * Находит задачи с указанной датой выполнения и статусом.
     *
     * @param dueDate дата выполнения задачи
     * @param completed статус выполнения задачи
     * @return список задач
     */
    List<Task> findByDueDateAndCompleted(LocalDate dueDate, boolean completed);

    /**
     * Находит задачи с датой выполнения в указанном диапазоне.
     *
     * @param start начальная дата диапазона (включительно)
     * @param end конечная дата диапазона (включительно)
     * @return список задач
     */
    List<Task> findByDueDateBetween(LocalDate start, LocalDate end);

    /**
     * Находит задачи с датой выполнения в указанном диапазоне и определенным статусом выполнения.
     *
     * @param start начальная дата диапазона (включительно)
     * @param end конечная дата диапазона (включительно)
     * @param completed статус выполнения задачи
     * @return список задач
     */
    List<Task> findByDueDateBetweenAndCompleted(LocalDate start, LocalDate end, boolean completed);

    /**
     * Находит задачи на текущую дату.
     *
     * @param includeCompleted включать ли выполненные задачи
     * @return список задач на сегодня
     */
    default List<Task> findTodayTasks(boolean includeCompleted) {
        LocalDate today = LocalDate.now();
        return includeCompleted ?
                findByDueDate(today) :
                findByDueDateAndCompleted(today, false);
    }

    /**
     * Находит задачи на текущую неделю (с сегодняшнего дня + 7 дней).
     *
     * @param includeCompleted включать ли выполненные задачи
     * @return список задач на неделю
     */
    default List<Task> findWeekTasks(boolean includeCompleted) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusWeeks(1);
        return includeCompleted ?
                findByDueDateBetween(start, end) :
                findByDueDateBetweenAndCompleted(start, end, false);
    }

    /**
     * Находит задачи на текущий месяц (с сегодняшнего дня + 30 дней).
     *
     * @param includeCompleted включать ли выполненные задачи
     * @return список задач на месяц
     */
    default List<Task> findMonthTasks(boolean includeCompleted) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(1);
        return includeCompleted ?
                findByDueDateBetween(start, end) :
                findByDueDateBetweenAndCompleted(start, end, false);
    }

    /**
     * Находит задачи на указанную дату с сортировкой по дате выполнения (по возрастанию).
     *
     * @param date дата выполнения задач
     * @return отсортированный список задач
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate = :date ORDER BY t.dueDate ASC")
    List<Task> findTasksByDateWithSorting(@Param("date") LocalDate date);

    /**
     * Находит задачи в указанном диапазоне дат с сортировкой по дате выполнения (по возрастанию).
     *
     * @param start начальная дата диапазона
     * @param end конечная дата диапазона
     * @return отсортированный список задач
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :start AND :end ORDER BY t.dueDate ASC")
    List<Task> findTasksByPeriodWithSorting(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    /**
     * Находит задачи в указанном диапазоне дат с определенным статусом выполнения,
     * с сортировкой по дате выполнения (по возрастанию).
     *
     * @param start начальная дата диапазона
     * @param end конечная дата диапазона
     * @param completed статус выполнения задачи
     * @return отсортированный список задач
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :start AND :end AND t.completed = :completed ORDER BY t.dueDate ASC")
    List<Task> findTasksByPeriodAndStatusWithSorting(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("completed") boolean completed);

    /**
     * Находит задачи, содержащие указанный текст в названии.
     *
     * @param title текст для поиска в названии задачи
     * @return список найденных задач
     */
    List<Task> findByTitleContaining(String title);

    /**
     * Находит задачи, содержащие указанный текст в названии и имеющие определенный статус выполнения.
     *
     * @param title текст для поиска в названии задачи
     * @param b статус выполнения задачи
     * @return список найденных задач
     */
    List<Task> findByTitleContainingAndCompleted(String title, boolean b);
}