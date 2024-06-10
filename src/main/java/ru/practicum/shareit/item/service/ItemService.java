package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemCreateDto itemCreateDto);

    ItemPresentDto findItem(int currentUserId, int itemId);

    ItemDto updateItem(ItemUpdateDto itemUpdateDto);

    List<ItemPresentDto> getUserItems(int userID, Pageable page);

    List<ItemDto> getAvailableItemsByName(String text, Pageable page);

    Item getItemById(int id);

    CommentDto postComment(CommentCreateDto commentCreateDto);

    List<ItemPresentForRequestDto> getItemsByRequestId(int id);
}