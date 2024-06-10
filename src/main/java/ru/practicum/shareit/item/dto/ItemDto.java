package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ItemDto {
    private int id;
    private int owner;
    private String name;
    private String description;
    private Boolean available;
    private int requestId;
}
