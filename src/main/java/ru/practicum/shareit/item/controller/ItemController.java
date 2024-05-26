package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dao.BookingOwnerRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

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
    public List<ItemPresentDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        List<ItemPresentDto> userItems = itemService.getUserItems(ownerId);
        log.info("User items: {}", userItems);
        return userItems;
    }

    @GetMapping("/search")
    public List<ItemDto> getAvailableItemsByName(@RequestParam String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        String textForSearch = text.toLowerCase();
        return itemService.getAvailableItemsByName(textForSearch);
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
}