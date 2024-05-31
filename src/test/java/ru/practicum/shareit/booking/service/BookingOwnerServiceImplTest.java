package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;
import ru.practicum.shareit.booking.controller.StateParam;
import ru.practicum.shareit.booking.dao.BookingOwnerRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.UnauthorizedUser;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ItemUtil;
import ru.practicum.shareit.util.UserUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class BookingOwnerServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private BookingOwnerRepository bookingOwnerRepository;
    @InjectMocks
    private BookingOwnerImpl bookingOwner;

    @Test
    @DisplayName("Test decision booking")
    public void givenBooking_thenApprove_thenReturnBookingDto() {
        //given
        LocalDateTime start = LocalDateTime.of(2000, 2, 2, 2, 2);
        LocalDateTime end = LocalDateTime.of(2000, 2, 3, 2, 2);

        User currentUser = UserUtil.getUser1();
        User booker = UserUtil.getUser2();

        Item item = ItemUtil.getItem1_WhereOwnerUser1();

        Booking booking = Booking.builder()
                .id(1)
                .booker(booker)
                .item(item)
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .build();

        Mockito.when(bookingOwnerRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        Mockito.when(bookingOwnerRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto obtainBookingDto = null;
        //when
        obtainBookingDto = bookingOwner.makeBookingDecision(currentUser.getId(), 1, true);

        //then
        assertThat(obtainBookingDto).isNotNull();
        assertThat(obtainBookingDto.getStatus()).isEqualTo(Status.APPROVED);

        //when
        obtainBookingDto = bookingOwner.makeBookingDecision(currentUser.getId(), 1, false);

        //then
        assertThat(obtainBookingDto).isNotNull();
        assertThat(obtainBookingDto.getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    @DisplayName("Test make decision when user is not owner")
    public void givenBooking_whenMakeDecision_thenExceptionIsThrow() {
        //given
        LocalDateTime start = LocalDateTime.of(2000, 2, 2, 2, 2);
        LocalDateTime end = LocalDateTime.of(2000, 2, 3, 2, 2);

        User currentUser = UserUtil.getUser1();
        User booker = UserUtil.getUser1();

        Item item = ItemUtil.getItem2_WhereOwnerUser2();

        Booking booking = Booking.builder()
                .id(1)
                .booker(booker)
                .item(item)
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .build();

        Mockito.when(bookingOwnerRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        //when
        UnauthorizedUser exception = assertThrows(UnauthorizedUser.class, () -> bookingOwner.makeBookingDecision(currentUser.getId(), 1, true));

        //then
        assertThat(exception.getMessage()).isEqualTo("Вы не можете изменить статус бронирования");
    }

    @Test
    @DisplayName("Test find bookings by condition")
    public void givenUserIdAndState_whenFindBookings_thenReturnBooking() {
        //given
        StateParam state = null;

        User user = UserUtil.getUser1();
        Item item = ItemUtil.getItem2_WhereOwnerUser2();

        int userId = user.getId();

        Mockito.when(userService.getCurrentUserById(anyInt())).thenReturn(user);

        Booking booking = Booking.builder()
                .id(2)
                .booker(User.builder()
                        .id(100)
                        .build())
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .status(Status.WAITING)
                .build();

        List<BookingDto> returnedBookings = null;

        //when
        Mockito.when(bookingOwnerRepository.findAllByItemOwnerIdOrderByStartDesc(anyInt())).thenReturn(List.of(booking));
        returnedBookings = bookingOwner.findBookingByCondition(userId, StateParam.ALL);

        //then
        assertThat(CollectionUtils.isEmpty(returnedBookings)).isFalse();

        //when
        Mockito.when(bookingOwnerRepository.findAllByItemOwnerIdAndEndLessThanOrderByStartDesc(anyInt(), any(LocalDateTime.class))).thenReturn(List.of(booking));
        returnedBookings = bookingOwner.findBookingByCondition(userId, StateParam.PAST);

        //then
        assertThat(CollectionUtils.isEmpty(returnedBookings)).isFalse();

        //when
        Mockito.when(bookingOwnerRepository.findAllByItemOwnerIdAndStartGreaterThanOrderByStartDesc(anyInt(), any(LocalDateTime.class))).thenReturn(List.of(booking));
        returnedBookings = bookingOwner.findBookingByCondition(userId, StateParam.FUTURE);

        //then
        assertThat(CollectionUtils.isEmpty(returnedBookings)).isFalse();

        //when
        Mockito.when(bookingOwnerRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyInt(), any(Status.class))).thenReturn(List.of(booking));
        returnedBookings = bookingOwner.findBookingByCondition(userId, StateParam.WAITING);

        //then
        assertThat(CollectionUtils.isEmpty(returnedBookings)).isFalse();

        //when
        Mockito.when(bookingOwnerRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyInt(), any(Status.class))).thenReturn(List.of(booking));
        returnedBookings = bookingOwner.findBookingByCondition(userId, StateParam.REJECTED);

        //then
        assertThat(CollectionUtils.isEmpty(returnedBookings)).isFalse();

        //when
        Mockito.when(bookingOwnerRepository.findCurrentBooking(anyInt(), any(LocalDateTime.class))).thenReturn(List.of(booking));
        returnedBookings = bookingOwner.findBookingByCondition(userId, StateParam.CURRENT);

        //then
        assertThat(CollectionUtils.isEmpty(returnedBookings)).isFalse();
    }


}
