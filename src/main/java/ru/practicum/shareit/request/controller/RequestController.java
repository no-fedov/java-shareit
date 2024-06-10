package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.util.Page.getPage;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestService requestService;
    private final UserService userService;

    @PostMapping
    public RequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Integer currentUserId,
                                 @Valid @RequestBody RequestCreateDto requestCreateDto) {
        requestCreateDto.setRequester(currentUserId);

        return requestService.addRequest(requestCreateDto);
    }

    @GetMapping
    public List<RequestDtoWithItems> getRequests(@RequestHeader("X-Sharer-User-Id") Integer currentUserId) {
        return requestService.getRequests(currentUserId);
    }

    @GetMapping("/{id}")
    public RequestDtoWithItems getRequest(@RequestHeader("X-Sharer-User-Id") Integer currentUserId,
                                          @PathVariable Integer id) {
        userService.getCurrentUserById(currentUserId);
        return requestService.getRequestById(id);
    }

    @GetMapping("/all")
    public List<RequestDtoWithItems> getPageRequest(@RequestHeader("X-Sharer-User-Id") Integer currentUserId,
                                                    @Min(0) @RequestParam(name = "from", required = false) Integer from,
                                                    @Min(1) @RequestParam(name = "size", required = false) Integer size) {

        Pageable page = getPage(from, size);

        return requestService.getPageRequest(currentUserId, page);
    }
}