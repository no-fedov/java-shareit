package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.exception.NoValidTime;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingCreateDto {

    @NotNull
    private int booker;

    @NotNull
    private int itemId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    private Status status = Status.WAITING;

    public void valid() {
        if (start.isEqual(end) || end.isBefore(start) || end.isBefore(LocalDateTime.now())
                || start.isBefore(LocalDateTime.now())) {
            throw new NoValidTime("Неверно указаны даты бронирования");
        }
    }
}
