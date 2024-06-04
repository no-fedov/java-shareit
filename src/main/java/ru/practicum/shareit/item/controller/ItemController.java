package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                           @Valid @RequestBody ItemCreateDto itemCreateDto) {

        itemCreateDto.setOwner(ownerId);

        ItemDto addedItem = itemService.addItem(itemCreateDto);
        log.info("Item added: {}", addedItem);
        return addedItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                              @RequestBody ItemUpdateDto itemUpdateDto,
                              @PathVariable int itemId) {

        itemUpdateDto.setId(itemId);
        itemUpdateDto.setOwner(ownerId);

        ItemDto updatedItem = itemService.updateItem(itemUpdateDto);
        log.info("Item updated {}", updatedItem);

        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ItemPresentDto getItemById(@RequestHeader("X-Sharer-User-Id") Integer currentUserId, @PathVariable("itemId") int id) {
        return itemService.findItem(currentUserId, id);
    }

    @GetMapping
    public List<ItemPresentDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                             @Min(0) @RequestParam(value = "from", required = false) Integer from,
                                             @Min(1) @RequestParam(value = "size", required = false) Integer size) {
        Pageable page = getPage(from, size);
        List<ItemPresentDto> userItems = itemService.getUserItems(ownerId, page);
        log.info("User items: {}", userItems);
        return userItems;
    }

    @GetMapping("/search")
    public List<ItemDto> getAvailableItemsByName(@RequestParam String text,
                                                 @Min(0) @RequestParam(value = "from", required = false) Integer from,
                                                 @Min(1) @RequestParam(value = "size", required = false) Integer size) {

        if (text == null || text.isBlank()) {
            return List.of();
        }
        Pageable page = getPage(from, size);

        String textForSearch = text.toLowerCase();
        return itemService.getAvailableItemsByName(textForSearch, page);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto postComment(@RequestHeader("X-Sharer-User-Id") Integer currentUser,
                                  @PathVariable("itemId") int itemId,
                                  @RequestBody @Valid CommentCreateDto commentCreateDto) {
        commentCreateDto.setAuthorId(currentUser);
        commentCreateDto.setItemId(itemId);
        commentCreateDto.setCreated(LocalDateTime.now());

        return itemService.postComment(commentCreateDto);
    }

    private Pageable getPage(Integer from, Integer size) {
        Pageable page = Pageable.unpaged();
        if (from != null && size != null) {
            page = PageRequest.of(from, size);
        }
        return page;
    }
}