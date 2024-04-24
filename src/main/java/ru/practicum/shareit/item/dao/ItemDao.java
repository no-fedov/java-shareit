package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {

    Item create(Item item);

    Optional<Item> read(int id);

    Item update(Item item);

    Item delete(int id);

    Optional<Item> findUserItem(int ownerId, int itemId);

    List<Item> getUserItems(int userID);

    List<Item> getAvailableItemsByName(String text);
}
