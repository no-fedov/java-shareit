package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingOwnerRepository;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.model.NotFoundEntityException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exception.CommentException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service("itemServiceImp")
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImp implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingOwnerRepository bookingOwnerRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private final UserService userService;
    private final ItemOwnerService itemOwnerService;

    @Override
    public ItemDto addItem(ItemCreateDto itemCreateDto) {
        log.info("ItemServiceImp: User add new item ({})", itemCreateDto);
        User currentUser = getCurrentUser(itemCreateDto.getOwner());
        Item newItem = ItemMapper.mapToItemFromItemCreateDto(itemCreateDto, currentUser);
        Item addedItem = itemRepository.save(newItem);
        return ItemMapper.mapToItemDtoFromItem(addedItem);
    }

    @Override
    public ItemPresentDto findItem(int currentUserId, int itemId) {
        log.info("ItemServiceImp: Request has been received to search for a item with an id = {}", itemId);

        Item item = getItemById(itemId);

        List<CommentDto> commentDtoList = commentRepository.findCommentsByItemId(itemId);

        BookingDto prev = null;
        BookingDto next = null;

        if (item.getOwner().getId() == currentUserId) {
            prev = BookingMapper.mapToBookingDtoFromBooking(bookingOwnerRepository.findPreviousBooking(item.getId(), LocalDateTime.now()));
            next = BookingMapper.mapToBookingDtoFromBooking(bookingOwnerRepository.findNextBooking(item.getId(), LocalDateTime.now()));
        }

        return ItemMapper.mapToItemPresentDtoFromItem(item, prev, next, commentDtoList);
    }

    @Override
    public ItemDto updateItem(ItemUpdateDto itemUpdateDto) {
        log.info("ItemServiceImp: updateItem itemID = {} where ownerID = {}", itemUpdateDto.getId(), itemUpdateDto.getOwner());

        User currentUser = getCurrentUser(itemUpdateDto.getOwner());

        Item item = itemRepository.findByIdAndOwnerId(itemUpdateDto.getId(), itemUpdateDto.getOwner())
                .orElseThrow(() -> new NotFoundEntityException(String.format("User with id = %s " +
                        "don't have item with = %s", itemUpdateDto.getOwner(), itemUpdateDto.getId())));

        Item updatedItem = ItemMapper.mapToItemFromItemUpdateDto(item, itemUpdateDto);
        updatedItem = itemRepository.save(updatedItem);
        return ItemMapper.mapToItemDtoFromItem(updatedItem);
    }

    @Override
    public List<ItemPresentDto> getUserItems(int userID) {
        return itemOwnerService.getUserItems(userID);
    }

    @Override
    public List<ItemDto> getAvailableItemsByName(String text) {
        List<Item> items = itemRepository.findByNameOrDescriptionContainingAllIgnoreCaseAndAvailable(text, text, true);
        return ItemMapper.mapToListItemDtoFromListItem(items);
    }

    @Override
    public Item getItemById(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException("Item not found"));
    }

    @Override
    public CommentDto postComment(CommentCreateDto commentCreateDto) {

        int currentUserId = commentCreateDto.getAuthorId();
        int currentItemId = commentCreateDto.getItemId();

        User currentUser = getCurrentUser(currentUserId);
        Item currentItem = getItemById(currentItemId);

        if (bookingRepository.findByBookerIdAndItemIdAndStatusAndStartLessThan(currentUserId, currentItemId,
                Status.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new CommentException("Нельзя оставить комментарий когда вы не бронировали вещь");
        }

        Comment comment = commentRepository.save(CommentMapper.mapToCommentFromCommentCreatedDto(commentCreateDto, currentUser, currentItem));

        return CommentMapper.mapToCommentDtoFromComment(comment);
    }

    private User getCurrentUser(int id) {
        return userService.getCurrentUserById(id);
    }
}