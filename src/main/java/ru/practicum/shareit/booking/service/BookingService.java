package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.controller.StateParam;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingCreateDto bookingCreateDto);

    BookingDto findBooking(int bookingId, int userId);

    List<BookingDto> findBookingByCondition(int userId, StateParam state);

}
