package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.NotFoundEntityException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemDaoImp implements ItemDao {
    private final Map<Integer, Item> itemRepository = new HashMap<>();
    private final Map<Integer, Map<Integer, Item>> userItems = new HashMap<>();
    private int generatorId = 0;

    @Override
    public Item create(Item item) {
        int id = generateID();
        Item newItem = item.toBuilder().id(id).build();
        itemRepository.put(id, newItem);
        userItems.computeIfAbsent(item.getOwner(), key -> new HashMap<>()).put(id, newItem);
        log.info("ItemDaoImp: add new item ({})", newItem);
        return newItem;
    }

    @Override
    public Optional<Item> read(int id) {
        log.info("ItemDaoImp: find item with id = {}", id);
        return Optional.ofNullable(itemRepository.get(id));
    }

    @Override
    public Item update(Item item) {
        log.info("ItemDaoImp: user with id = {} updated ({})", item.getId(), item);
        itemRepository.put(item.getId(), item);
        userItems.get(item.getOwner()).put(item.getId(), item);
        return item;
    }

    @Override
    public Item delete(int id) {
        log.info("ItemDaoImp: delete user with id = {}", id);
        return itemRepository.remove(id);
    }

    @Override
    public Optional<Item> findUserItem(int ownerId, int itemId) {
        //npe
        if (userItems.get(ownerId) == null) {
            throw new NotFoundEntityException("User not found");
        }
        return Optional.ofNullable(userItems.get(ownerId).get(itemId));
    }

    @Override
    public List<Item> getUserItems(int userID) {
        return userItems.get(userID).values().stream()
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Item> getAvailableItemsByName(String text) {
        return itemRepository.values().stream().filter(item -> item.getAvailable()
                        && (item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text)))
                .collect(Collectors.toUnmodifiableList());
    }

    private int generateID() {
        return ++generatorId;
    }
}
