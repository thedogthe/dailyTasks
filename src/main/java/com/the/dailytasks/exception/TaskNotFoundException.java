package com.the.dailytasks.exception;

// Класс для обработки исключения TaskNotFound
public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}