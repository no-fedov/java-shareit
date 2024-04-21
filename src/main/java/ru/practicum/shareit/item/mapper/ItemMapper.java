package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    private ItemMapper() {
    }

    public static void updateItem(Item item, ItemUpdateDto itemUpdateDto) {
        if (itemUpdateDto == null) {
            return;
        }

        String newName = itemUpdateDto.getName();

        if (newName != null && !newName.isBlank()) {
            item.setName(newName);
        }

        String newDescription = itemUpdateDto.getDescription();

        if (newDescription != null && !newDescription.isBlank()) {
            item.setDescription(newDescription);
        }

        Boolean newStatus = itemUpdateDto.getAvailable();

        if (newStatus != null) {
            item.setAvailable(newStatus);
        }
    }
}
