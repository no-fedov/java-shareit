package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.controller.StateParam;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.exception.ItemNotAvailableException;
import ru.practicum.shareit.booking.exception.UnauthorizedUser;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.model.NotFoundEntityException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDto addBooking(BookingCreateDto bookingCreateDto) {
        User currentUser = getCurrentUser(bookingCreateDto.getBooker());
        Item item = itemService.getItemById(bookingCreateDto.getItemId());

        if (item.getOwner().getId() == currentUser.getId()) {
            throw new BookingException("Нельзя забронировать свою же вещь");
        }

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException();
        }

        Booking booking = BookingMapper.mapToBookingFromBookingCreateDto(bookingCreateDto, currentUser, item);
        booking = bookingRepository.save(booking);

        return BookingMapper.mapToBookingDtoFromBooking(booking);
    }

    @Override
    public BookingDto findBooking(int bookingId, int userId) {
        Booking booking = getBooking(bookingId);

        if (!(userId == booking.getBooker().getId()
                || userId == booking.getItem().getOwner().getId())) {
            throw new UnauthorizedUser("Вы не можете просматривать данные этого бронирования");
        }

        return BookingMapper.mapToBookingDtoFromBooking(booking);
    }

    @Override
    public List<BookingDto> findBookingByCondition(int userId, StateParam state) {

        getCurrentUser(userId);

        switch (state) {
            case ALL:
                List<Booking> allBookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                return BookingMapper.mapToListBookingDtoFromListBooking(allBookings);

            case PAST:
                List<Booking> currentBookings = bookingRepository.findByBookerIdAndEndLessThanOrderByStartDesc(userId, LocalDateTime.now());
                return BookingMapper.mapToListBookingDtoFromListBooking(currentBookings);

            case FUTURE:
                List<Booking> futureBookings = bookingRepository.findByBookerIdAndStartGreaterThanOrderByStartDesc(userId, LocalDateTime.now());
                return BookingMapper.mapToListBookingDtoFromListBooking(futureBookings);

            case WAITING:
                List<Booking> waitingBookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                return BookingMapper.mapToListBookingDtoFromListBooking(waitingBookings);

            case REJECTED:
                List<Booking> rejectedBookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                return BookingMapper.mapToListBookingDtoFromListBooking(rejectedBookings);

            case CURRENT:
                List<Booking> currentBooking = bookingRepository.findCurrentBooking(LocalDateTime.now(), userId);
                return BookingMapper.mapToListBookingDtoFromListBooking(currentBooking);
        }

        return List.of();
    }

    private User getCurrentUser(int id) {
        return userService.getCurrentUserById(id);
    }

    private Booking getBooking(int id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException("Booking not found"));
    }
}
