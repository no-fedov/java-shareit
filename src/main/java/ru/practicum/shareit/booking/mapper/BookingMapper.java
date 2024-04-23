package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPresent;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking mapToBookingFromBookingCreateDto(BookingCreateDto bookingCreateDto, User user, Item item) {
        return Booking.builder()
                .booker(user)
                .item(item)
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .status(Status.WAITING)
                .build();
    }

    public static BookingDto mapToBookingDtoFromBooking(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDto.builder()
                .id(booking.getId())
                .booker(UserMapper.mapToUserDtoFromUser(booking.getBooker()))
                .item(ItemMapper.mapToItemDtoFromItem(booking.getItem()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    public static BookingPresent mapToBookingPresentFromBookingDto(BookingDto bookingDto) {
        if (bookingDto == null) {
            return null;
        }
        return BookingPresent.builder()
                .id(bookingDto.getId())
                .bookerId(bookingDto.getBooker().getId())
                .build();
    }

    public static List<BookingDto> mapToListBookingDtoFromListBooking(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::mapToBookingDtoFromBooking)
                .collect(Collectors.toList());
    }
}
