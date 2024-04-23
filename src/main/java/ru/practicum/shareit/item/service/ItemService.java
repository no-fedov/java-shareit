package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemCreateDto itemCreateDto);

    ItemPresentDto findItem(int currentUserId, int itemId);

    ItemDto updateItem(ItemUpdateDto itemUpdateDto);

    List<ItemPresentDto> getUserItems(int userID);

    List<ItemDto> getAvailableItemsByName(String text);

    Item getItemById(int id);

    CommentDto postComment(CommentCreateDto commentCreateDto);
}
