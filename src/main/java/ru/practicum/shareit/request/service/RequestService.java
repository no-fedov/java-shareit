package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(RequestCreateDto itemRequestDto);

    List<RequestDtoWithItems> getRequests(int userId);

    RequestDtoWithItems getRequestById(int id);

    List<RequestDtoWithItems> getPageRequest(int userId, int from, int size);
}
