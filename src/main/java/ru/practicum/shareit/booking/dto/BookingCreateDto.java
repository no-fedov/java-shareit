package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.exception.NoValidTime;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class BookingCreateDto {

    private int booker;

    @NotNull
    private Integer itemId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    private Status status = Status.WAITING;

    public void valid() {
        LocalDateTime now = LocalDateTime.now();
        if (start.isEqual(end) || end.isBefore(start) || end.isBefore(now)
                || start.isBefore(now)) {
            throw new NoValidTime("Неверно указаны даты бронирования");
        }
    }
}
