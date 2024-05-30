package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BookingPresent {
    private int id;
    private int bookerId;
}
