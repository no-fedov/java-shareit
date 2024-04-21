package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final UserService userService;
    private final ItemService itemService;

    @PostMapping
    public Item addItem(@RequestHeader("X-Sharer-User-Id") Optional<Integer> userID,
                        @Valid @RequestBody Item item) {
        int ownerId = userID.orElseThrow(RuntimeException::new);

        userService.findUser(ownerId);
        Item newItem = item.toBuilder().owner(ownerId).build();
        Item addedItem = itemService.addItem(newItem);
        log.info("Item added: {}", addedItem);
        return addedItem;
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") Optional<Integer> owner,
                           @RequestBody ItemUpdateDto itemUpdateDto,
                           @PathVariable int itemId) {

        int ownerId = owner.orElseThrow(RuntimeException::new);
        itemUpdateDto.setId(itemId);
        itemUpdateDto.setOwner(ownerId);
        Item updatedItem = itemService.updateItem(itemUpdateDto);
        log.info("Item updated {}", updatedItem);
        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable("itemId") int id) {
        return itemService.findItem(id);
    }

    @GetMapping
    public List<Item> getUserItems(@RequestHeader("X-Sharer-User-Id") Optional<Integer> userID) {
        int ownerId = userID.orElseThrow(RuntimeException::new);
        List<Item> userItems = itemService.getUserItems(ownerId);
        log.info("User items: {}", userItems);
        return userItems;
    }

    @GetMapping("/search")
    public List<Item> getAvailableItemsByName(@RequestParam Map<String, String> allParams) {
        String text = allParams.get("text");
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String textForSearch = allParams.get("text").toLowerCase();
        return itemService.getAvailableItemsByName(textForSearch);
    }
}
