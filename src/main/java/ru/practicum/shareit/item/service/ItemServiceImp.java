package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundEntityException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Service("itemServiceImp")
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImp implements ItemService {
    private final ItemDao itemDao;

    @Override
    public ItemDto addItem(ItemCreateDto itemCreateDto) {
        log.info("ItemServiceImp: User add new item ({})", itemCreateDto);
        Item newItem = ItemMapper.mapToItemFromItemCreateDto(itemCreateDto);
        Item addedItem = itemDao.create(newItem);
        return ItemMapper.mapToItemDtoFromItem(addedItem);
    }

    @Override
    public ItemDto findItem(int id) {
        log.info("ItemServiceImp: Request has been received to search for a item with an id = {}", id);
        Item item = getItemById(id);
        log.info("ItemServiceImp: found item ({})", item);
        return ItemMapper.mapToItemDtoFromItem(item);
    }

    @Override
    public ItemDto updateItem(ItemUpdateDto itemUpdateDto) {
        log.info("ItemServiceImp: updateItem itemID = {} where ownerID = {}", itemUpdateDto.getId(), itemUpdateDto.getOwner());
        Item item = itemDao.findUserItem(itemUpdateDto.getOwner(), itemUpdateDto.getId())
                .orElseThrow(() -> new NotFoundEntityException(String.format("User with id = %s " +
                        "don't have item with = %s", itemUpdateDto.getOwner(), itemUpdateDto.getId())));
        Item updatedItem = ItemMapper.mapToItemFromItemUpdateDto(item, itemUpdateDto);
        return ItemMapper.mapToItemDtoFromItem(updatedItem);
    }

    @Override
    public List<ItemDto> getUserItems(int userID) {
        return itemDao.getUserItems(userID).stream()
                .map(ItemMapper::mapToItemDtoFromItem)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ItemDto> getAvailableItemsByName(String text) {
        return itemDao.getAvailableItemsByName(text).stream()
                .map(ItemMapper::mapToItemDtoFromItem)
                .collect(Collectors.toUnmodifiableList());
    }

    private Item getItemById(int id) {
        return itemDao.read(id).orElseThrow(() -> new NotFoundEntityException("Item not found"));
    }
}
