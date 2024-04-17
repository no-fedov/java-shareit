package ru.practicum.shareit.user.validation;

public class EmailIsBusy extends RuntimeException {
    public EmailIsBusy(String message) {
        super(message);
    }
}
