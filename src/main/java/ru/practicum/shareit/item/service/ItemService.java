package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemCreateDto itemCreateDto);

    ItemDto findItem(int id);

    ItemDto updateItem(ItemUpdateDto itemUpdateDto);

    List<ItemDto> getUserItems(int userID);

    List<ItemDto> getAvailableItemsByName(String text);
}
