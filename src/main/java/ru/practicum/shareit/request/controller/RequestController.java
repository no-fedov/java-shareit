package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

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
        return requestService.getRequestById(id);
    }

    @GetMapping("/all")
    public List<RequestDtoWithItems> getPageRequest(@RequestHeader("X-Sharer-User-Id") Integer currentUserId,
                                              @Min(0) @RequestParam("from") Integer from,
                                              @Min(1) @RequestParam("size") Integer size) {

        PageRequest page = PageRequest.of(from, size);

        return null;
    }
//    GET /requests/all?from={from}&size={size}
}
