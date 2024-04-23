package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingPresent;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class ItemPresentDto  {
    private int id;
    private int owner;
    private String name;
    private String description;
    private Boolean available;
    private BookingPresent lastBooking;
    private BookingPresent nextBooking;
    private List<CommentDto> comments;
}
