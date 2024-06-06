package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundEntityException;
import ru.practicum.shareit.item.dto.ItemPresentForRequestDto;
import ru.practicum.shareit.item.service.ItemOwnerService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemOwnerService itemOwnerService;
    private final ItemService itemService;

    @Override
    public RequestDto addRequest(RequestCreateDto requestCreateDto) {

        User currentUser = getCurrentUser(requestCreateDto.getRequester());

        Request newRequest = Request.builder()
                .requester(currentUser)
                .description(requestCreateDto.getDescription())
                .created(LocalDateTime.now())
                .build();

        Request savedRequest = requestRepository.save(newRequest);

        return RequestMapper.mapToItemRequestDtoFromItemRequest(savedRequest, currentUser.getId());
    }

    @Override
    public List<RequestDtoWithItems> getRequests(int userId) {

        getCurrentUser(userId);

        List<Request> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);

        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        List<Integer> requestIds = requests.stream().map(Request::getId).collect(Collectors.toList());

        List<ItemPresentForRequestDto> ipfr = itemOwnerService.getItemsByRequestIds(requestIds);

        Map<Integer, List<ItemPresentForRequestDto>> items = ipfr.stream()
                .collect(Collectors.groupingBy(ItemPresentForRequestDto::getRequestId));

        return RequestMapper.mapToRequestDtoWithItemsFromRequest(requests, items);
    }

    @Override
    public RequestDtoWithItems getRequestById(int id) {
        Request request = getCurrentRequest(id);
        var items = itemService.getItemsByRequestId(id);
        return RequestMapper.mapToRequestDtoWithItemsFromRequest(request, items);
    }

    @Override
    public List<RequestDtoWithItems> getPageRequest(int userId, Pageable page) {
        userService.getCurrentUserById(userId);

        List<Request> pageRequest = requestRepository.findByRequesterIdNot(userId, page);
        List<Integer> requestIds = pageRequest.stream().map(Request::getId).collect(Collectors.toList());
        List<ItemPresentForRequestDto> ipfr = itemOwnerService.getItemsByRequestIds(requestIds);

        Map<Integer, List<ItemPresentForRequestDto>> items = ipfr.stream()
                .collect(Collectors.groupingBy(ItemPresentForRequestDto::getRequestId));

        return RequestMapper.mapToRequestDtoWithItemsFromRequest(pageRequest, items);
    }

    private User getCurrentUser(int userId) {
        return userService.getCurrentUserById(userId);
    }

    private Request getCurrentRequest(int requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundEntityException("Request is not exist"));
    }
}