package ru.practicum.shareit.user.exception.validation;

public class EmailIsBusy extends RuntimeException {
    public EmailIsBusy(String message) {
        super(message);
    }
}
