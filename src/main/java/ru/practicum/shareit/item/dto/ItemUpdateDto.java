package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ItemUpdateDto {
    private int id;
    private int owner;
    private String name;
    private String description;
    private Boolean available;
}
