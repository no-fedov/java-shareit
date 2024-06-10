package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemPresentForRequestDto;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static RequestDto mapToItemRequestDtoFromItemRequest(Request request, int requesterId) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requester(requesterId)
                .created(request.getCreated())
                .build();
    }

    public static RequestDtoWithItems mapToRequestDtoWithItemsFromRequest(Request request, List<ItemPresentForRequestDto> items) {
        return RequestDtoWithItems.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items)
                .build();
    }

    public static List<RequestDtoWithItems> mapToRequestDtoWithItemsFromRequest(List<Request> requests,
                                                                                Map<Integer, List<ItemPresentForRequestDto>> items) {
        List<RequestDtoWithItems> r = new ArrayList<>();

        for (Request request : requests) {
            int requestId = request.getId();
            var itemsList = items.getOrDefault(requestId, List.of());

            var requestForPresent = RequestDtoWithItems.builder()
                    .id(requestId)
                    .description(request.getDescription())
                    .created(request.getCreated())
                    .items(itemsList)
                    .build();

            r.add(requestForPresent);

        }

        return r;
    }

    public static Request mapToRequestFromRequestCreateDto(RequestCreateDto requestCreateDto, User user) {
        return Request.builder()
                .requester(user)
                .description(requestCreateDto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }
}
