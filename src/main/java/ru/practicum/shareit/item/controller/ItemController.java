package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final UserService userService;
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") int ownerId,
                           @Valid @RequestBody ItemCreateDto itemCreateDto) {
        userService.findUser(ownerId);
        itemCreateDto.setOwner(ownerId);

        ItemDto addedItem = itemService.addItem(itemCreateDto);
        log.info("Item added: {}", addedItem);
        return addedItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int ownerId,
                              @RequestBody ItemUpdateDto itemUpdateDto,
                              @PathVariable int itemId) {

        itemUpdateDto.setId(itemId);
        itemUpdateDto.setOwner(ownerId);

        ItemDto updatedItem = itemService.updateItem(itemUpdateDto);
        log.info("Item updated {}", updatedItem);

        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") int id) {
        return itemService.findItem(id);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        List<ItemDto> userItems = itemService.getUserItems(ownerId);
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
}
