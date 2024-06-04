package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Builder(toBuilder = true)
public class RequestDto {
    private int id;
    private int requester;
    private String description;
    private LocalDateTime created;
    private ItemDto itemDto;
}
