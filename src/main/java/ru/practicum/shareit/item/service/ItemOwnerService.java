package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.ItemPresentDto;
import ru.practicum.shareit.item.dto.ItemPresentForRequestDto;

import java.util.Collection;
import java.util.List;

public interface ItemOwnerService {
    List<ItemPresentDto> getUserItems(int userID);

    List<ItemPresentForRequestDto> getItemsByRequestIds(Collection<Integer> requestId);
}
