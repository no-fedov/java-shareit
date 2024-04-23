package ru.practicum.shareit.booking.exception;

public class NoValidTime extends RuntimeException {
    public NoValidTime(String message) {
        super(message);
    }
}
