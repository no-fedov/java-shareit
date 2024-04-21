package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundEntityException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service("itemServiceImp")
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImp implements ItemService {
    private final ItemDao itemDao;

    @Override
    public Item addItem(Item item) {
        log.info("ItemServiceImp: User add new item ({})", item);
        Item newItem = itemDao.create(item);
        return newItem;
    }

    @Override
    public Item findItem(int id) {
        log.info("ItemServiceImp: Request has been received to search for a item with an id = {}", id);
        Item item = itemDao.read(id).orElseThrow(() -> new NotFoundEntityException("Item not found"));
        log.info("ItemServiceImp: found item ({})", item);
        return item;
    }

    @Override
    public Item updateItem(ItemUpdateDto itemUpdateDto) {
        log.info("ItemServiceImp: updateItem itemID = {} where ownerID = {}", itemUpdateDto.getId(), itemUpdateDto.getOwner());
        Item item = itemDao.findUserItem(itemUpdateDto.getOwner(), itemUpdateDto.getId())
                .orElseThrow(() -> new NotFoundEntityException(String.format("User with id = %s " +
                        "don't have item with = %s", itemUpdateDto.getOwner(), itemUpdateDto.getId())));
        ItemMapper.updateItem(item, itemUpdateDto);
        return item;
    }

    @Override
    public List<Item> getUserItems(int userID) {
        return itemDao.getUserItems(userID);
    }

    @Override
    public List<Item> getAvailableItemsByName(String text) {
        return itemDao.getAvailableItemsByName(text);
    }
}
