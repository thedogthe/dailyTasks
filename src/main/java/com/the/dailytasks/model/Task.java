package com.the.dailytasks.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

/**
 * Класс, представляющий задачу в системе.
 * Содержит информацию о названии, описании, статусе выполнения и сроке выполнения задачи.
 * Аннотации Lombok используются для автоматической генерации геттеров, сеттеров,
 * конструкторов и builder-паттерна.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    /**
     * Уникальный идентификатор задачи.
     * Генерируется автоматически при сохранении в базу данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название задачи. Обязательное поле.
     * Не может быть пустым или состоять только из пробелов.
     */
    @NotBlank(message = "Title is mandatory")
    private String title;

    /**
     * Описание задачи. Может быть пустым.
     * Содержит дополнительную информацию о задаче.
     */
    private String description;

    /**
     * Флаг, указывающий статус выполнения задачи.
     * true - задача выполнена, false - задача не выполнена.
     */
    private boolean completed;

    /**
     * Срок выполнения задачи. Обязательное поле.
     * Не может быть null.
     */
    @NotNull(message = "Due date is mandatory")
    private LocalDate dueDate;
}