package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static Item mapToItemFromItemUpdateDto(Item item, ItemUpdateDto itemUpdateDto) {
        if (itemUpdateDto == null) {
            return item;
        }

        String newName = itemUpdateDto.getName();

        if (newName != null && !newName.isBlank()) {
            item.setName(newName);
        }

        String newDescription = itemUpdateDto.getDescription();

        if (newDescription != null && !newDescription.isBlank()) {
            item.setDescription(newDescription);
        }

        Boolean newStatus = itemUpdateDto.getAvailable();

        if (newStatus != null) {
            item.setAvailable(newStatus);
        }

        return item;
    }

    public static Item mapToItemFromItemCreateDto(ItemCreateDto itemCreateDto, User user) {
        return Item.builder()
                .owner(user)
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
                .build();
    }

    public static ItemDto mapToItemDtoFromItem(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .owner(item.getOwner().getId())
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())
                .build();
    }

    public static ItemPresentDto mapToItemPresentDtoFromItem(Item item, BookingDto prev, BookingDto next, List<CommentDto> commentDtoList) {
        return ItemPresentDto.builder()
                .id(item.getId())
                .owner(item.getOwner().getId())
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())
                .lastBooking(BookingMapper.mapToBookingPresentFromBookingDto(prev))
                .nextBooking(BookingMapper.mapToBookingPresentFromBookingDto(next))
                .comments(commentDtoList)
                .build();
    }

    public static List<ItemDto> mapToListItemDtoFromListItem(List<Item> items) {
        return items.stream()
                .map(ItemMapper::mapToItemDtoFromItem)
                .collect(Collectors.toUnmodifiableList());
    }
}