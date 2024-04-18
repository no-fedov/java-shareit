package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class Booking {
    private final int id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    private final int item;
    private final int booker;
    @NotNull
    private Status status;

    public enum Status {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED
    }
}
