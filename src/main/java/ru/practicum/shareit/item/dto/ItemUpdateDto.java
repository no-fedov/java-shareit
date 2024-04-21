package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemUpdateDto {
    private int id;
    private int owner;
    private String name;
    private String description;
    private Boolean available;
}
