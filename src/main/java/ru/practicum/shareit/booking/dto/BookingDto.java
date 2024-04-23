package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@Builder(toBuilder = true)
public class BookingDto {
    private int id;
    private UserDto booker;
    private ItemDto item;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
}
