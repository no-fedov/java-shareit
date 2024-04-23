package ru.practicum.shareit.booking.exception;

public class ChangeStatusException extends RuntimeException {
    public ChangeStatusException(String message) {
        super(message);
    }
}
