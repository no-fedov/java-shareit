package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public List<BookingDto> findBookingByCondition(int userId, StateParam state, Pageable page) {
        userService.getCurrentUserById(userId);
        switch (state) {
            case ALL:
                return BookingMapper.mapToListBookingDtoFromListBooking(
                        bookingOwnerRepository.findAllByItemOwnerIdOrderByStartDesc(userId, page)
                );

            case PAST:
                return BookingMapper.mapToListBookingDtoFromListBooking(
                        bookingOwnerRepository.findAllByItemOwnerIdAndEndLessThanOrderByStartDesc(userId, LocalDateTime.now(), page)
                );

            case FUTURE:
                return BookingMapper.mapToListBookingDtoFromListBooking(
                        bookingOwnerRepository.findAllByItemOwnerIdAndStartGreaterThanOrderByStartDesc(userId, LocalDateTime.now(), page)
                );

            case WAITING:
                return BookingMapper.mapToListBookingDtoFromListBooking(
                        bookingOwnerRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING, page)
                );

            case REJECTED:
                return BookingMapper.mapToListBookingDtoFromListBooking(
                        bookingOwnerRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page)
                );

            case CURRENT:
                return BookingMapper.mapToListBookingDtoFromListBooking(
                        bookingOwnerRepository.findCurrentBooking(userId, LocalDateTime.now(), page)
                );

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
