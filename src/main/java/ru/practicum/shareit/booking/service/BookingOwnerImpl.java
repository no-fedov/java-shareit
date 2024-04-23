package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.controller.StateParam;
import ru.practicum.shareit.booking.dao.BookingOwnerRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.ChangeStatusException;
import ru.practicum.shareit.booking.exception.UnauthorizedUser;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.model.NotFoundEntityException;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingOwnerImpl implements BookingOwnerService {
    private final UserService userService;
    private final BookingOwnerRepository bookingOwnerRepository;

    @Override
    public BookingDto makeBookingDecision(int ownerItemId, int bookingId, boolean approved) {
        Booking booking = getBooking(bookingId);

        if (booking.getItem().getOwner().getId() != ownerItemId) {
            throw new UnauthorizedUser("Вы не можете изменить статус бронирования");
        }

        Status status = approved ? Status.APPROVED : Status.REJECTED;

        if (booking.getStatus() == status) {
            throw new ChangeStatusException("Нельзя дважды ставить одинаковый статус подтверждения бронирования");
        }

        booking.setStatus(status);

        booking = bookingOwnerRepository.save(booking);

        return BookingMapper.mapToBookingDtoFromBooking(booking);
    }

    @Override
    public List<BookingDto> findBookingByCondition(int userId, StateParam state) {
        userService.getCurrentUserById(userId);
        switch (state) {
            case ALL:
                List<Booking> allBookings = bookingOwnerRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                return BookingMapper.mapToListBookingDtoFromListBooking(allBookings);

            case PAST:
                List<Booking> pastBookings = bookingOwnerRepository.findAllByItemOwnerIdAndEndLessThanOrderByStartDesc(userId, LocalDateTime.now());
                return BookingMapper.mapToListBookingDtoFromListBooking(pastBookings);

            case FUTURE:
                List<Booking> futureBookings = bookingOwnerRepository.findAllByItemOwnerIdAndStartGreaterThanOrderByStartDesc(userId, LocalDateTime.now());
                return BookingMapper.mapToListBookingDtoFromListBooking(futureBookings);

            case WAITING:
                List<Booking> waitingBookings = bookingOwnerRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                return BookingMapper.mapToListBookingDtoFromListBooking(waitingBookings);

            case REJECTED:
                List<Booking> rejectedBookings = bookingOwnerRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                return BookingMapper.mapToListBookingDtoFromListBooking(rejectedBookings);

            case CURRENT:
                List<Booking> currentBookings = bookingOwnerRepository.findCurrentBooking(LocalDateTime.now());
                return BookingMapper.mapToListBookingDtoFromListBooking(currentBookings);

        }

        return List.of();
    }

    @Override
    public BookingDto findPreviousBooking(int itemId, LocalDateTime date) {
        Booking prevBooking = bookingOwnerRepository.findPreviousBooking(itemId, date);
        return BookingMapper.mapToBookingDtoFromBooking(prevBooking);
    }

    @Override
    public BookingDto findNextBooking(int itemId, LocalDateTime date) {
        Booking nextBooking = bookingOwnerRepository.findNextBooking(itemId, date);
        return BookingMapper.mapToBookingDtoFromBooking(nextBooking);
    }

    private Booking getBooking(int id) {
        return bookingOwnerRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException("Booking not found"));
    }
}
