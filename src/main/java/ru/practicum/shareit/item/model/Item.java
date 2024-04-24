package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Item {
    private final int id;
    private final int owner;
    private String name;
    private String description;
    private Boolean available;
}
