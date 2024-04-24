package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static Item mapToItemFromItemUpdateDto(Item item, ItemUpdateDto itemUpdateDto) {
        if (itemUpdateDto == null) {
            return item;
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

        return item;
    }

    public static Item mapToItemFromItemCreateDto(ItemCreateDto itemCreateDto) {
        return Item.builder()
                .owner(itemCreateDto.getOwner())
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
                .build();

    }

    public static ItemDto mapToItemDtoFromItem(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())
                .build();
    }
}
