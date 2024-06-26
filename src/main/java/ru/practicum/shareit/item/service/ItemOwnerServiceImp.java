package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingOwnerService;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemPresentDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ItemOwnerServiceImp implements ItemOwnerService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    private final BookingOwnerService bookingOwnerService;

    @Override
    public List<ItemPresentDto> getUserItems(int userID) {
        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userID);

        List<ItemPresentDto> itemOwnerDtoList = new ArrayList<>();
        for (Item item : items) {

            BookingDto prev = bookingOwnerService.findPreviousBooking(item.getId(), LocalDateTime.now());
            BookingDto next = bookingOwnerService.findNextBooking(item.getId(), LocalDateTime.now());
            List<CommentDto> comments = commentRepository.findCommentsByItemId(item.getId());

            ItemPresentDto itemOwnerDto = ItemMapper.mapToItemPresentDtoFromItem(item, prev, next, comments);
            itemOwnerDtoList.add(itemOwnerDto);
        }

        return itemOwnerDtoList;
    }
}
