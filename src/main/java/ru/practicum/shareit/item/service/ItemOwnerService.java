package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPresentDto;

import java.util.List;

public interface ItemOwnerService {
    List<ItemPresentDto> getUserItems(int userID);
}
