package ru.practicum.shareit.exception.model;

public class NotFoundEntityException extends RuntimeException {
    public NotFoundEntityException(String message) {
        super(message);
    }
}
