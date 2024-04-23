package ru.practicum.shareit.booking.exception;

public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException() {
        super("Item not available");
    }
}
