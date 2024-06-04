package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class ItemPresentForRequestDto {
    private int id;
    private String description;
    private int requestId;
    private boolean available;
}
