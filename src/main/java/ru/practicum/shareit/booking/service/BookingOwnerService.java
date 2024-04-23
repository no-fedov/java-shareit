package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.controller.StateParam;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingOwnerService {
    BookingDto makeBookingDecision(int ownerItemId, int bookingId, boolean approved);

    BookingDto findPreviousBooking(int itemId, LocalDateTime date);

    BookingDto findNextBooking(int itemId, LocalDateTime date);

    List<BookingDto> findBookingByCondition(int userId, StateParam stateParam);

}
