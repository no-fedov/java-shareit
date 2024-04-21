package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Item item);

    Item findItem(int id);

    Item updateItem(ItemUpdateDto itemUpdateDto);

    List<Item> getUserItems(int userID);

    List<Item> getAvailableItemsByName(String text);
}
