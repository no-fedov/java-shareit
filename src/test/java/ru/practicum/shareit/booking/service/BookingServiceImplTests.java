package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import ru.practicum.shareit.booking.controller.StateParam;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.exception.ItemNotAvailableException;
import ru.practicum.shareit.booking.exception.UnauthorizedUser;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
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
public class BookingServiceImplTests {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("Test add booking functionality")
    public void givenBookingCreateDto_whenSaveBooking_thenReturnSavedBooking() {
        //given
        LocalDateTime start = LocalDateTime.of(2000, 1, 1, 1, 1);
        LocalDateTime end = start.plusDays(1);

        User currentUser = UserUtil.getUser1();
        Item item = ItemUtil.getItem2_WhereOwnerUser2();

        UserDto currentUserDto = UserMapper.mapToUserDtoFromUser(currentUser);
        ItemDto itemDto = ItemMapper.mapToItemDtoFromItem(item);

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(2)
                .booker(1)
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .build();

        Booking booking = Booking.builder()
                .id(1)
                .booker(currentUser)
                .item(item)
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(1)
                .booker(currentUserDto)
                .item(itemDto)
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .build();

        Mockito.when(userService.getCurrentUserById(anyInt())).thenReturn(currentUser);
        Mockito.when(itemService.getItemById(anyInt())).thenReturn(item);
        Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        //when
        BookingDto obtainedBookingDto = bookingService.addBooking(bookingCreateDto);

        //then
        assertThat(obtainedBookingDto).isNotNull();
        assertThat(obtainedBookingDto).isEqualTo(bookingDto);
    }

    @Test
    @DisplayName("Test add booking your own item functionality")
    public void givenBookingCreateDto_whenSaveBooking_thenExceptionIsThrow() {
        //given
        LocalDateTime start = LocalDateTime.of(2000, 1, 1, 1, 1);
        LocalDateTime end = start.plusDays(1);

        User currentUser1 = UserUtil.getUser1();
        Item item1 = ItemUtil.getItem1_WhereOwnerUser1();

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(1)
                .booker(1)
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .build();


        Mockito.when(userService.getCurrentUserById(anyInt())).thenReturn(currentUser1);
        Mockito.when(itemService.getItemById(anyInt())).thenReturn(item1);

        //when
        BookingException exception = assertThrows(BookingException.class,
                () -> bookingService.addBooking(bookingCreateDto));

        //then
        assertThat(exception.getMessage()).isEqualTo("Нельзя забронировать свою же вещь");
    }

    @Test
    @DisplayName("Test add booking for unavailable item functionality")
    public void givenBookingCreateDtoForUnavailableItem_whenSaveBooking_thenExceptionIsThrow() {
        //given
        LocalDateTime start = LocalDateTime.of(2000, 1, 1, 1, 1);
        LocalDateTime end = start.plusDays(1);

        User currentUser1 = UserUtil.getUser1();

        Item item1 = Item.builder()
                .id(2)
                .name("item2")
                .available(false)
                .owner(UserUtil.getUser2())
                .build();

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(2)
                .booker(1)
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .build();


        Mockito.when(userService.getCurrentUserById(anyInt())).thenReturn(currentUser1);
        Mockito.when(itemService.getItemById(anyInt())).thenReturn(item1);

        //when
        ItemNotAvailableException exception = assertThrows(ItemNotAvailableException.class,
                () -> bookingService.addBooking(bookingCreateDto));

        //then
        assertThat(exception.getMessage()).isEqualTo("Item not available");
    }

    @Test
    @DisplayName("Test find booking functionality")
    public void givenBookingIdAndUserId_whenFindBooking_thenReturnBooking() {
        //given
        User currentUser = UserUtil.getUser1();
        Item item = ItemUtil.getItem2_WhereOwnerUser2();

        int userId = 1;
        int bookingId = 2;

        Booking booking = Booking.builder()
                .id(bookingId)
                .booker(currentUser)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .status(Status.WAITING)
                .build();

        Mockito.when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        //when
        BookingDto bookingDto = bookingService.findBooking(bookingId, userId);

        //then
        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getItem().getName()).isEqualTo(item.getName());
    }

    @Test
    @DisplayName("Test find booking functionality")
    public void givenBookingIdAndUserId_whenFindBooking_thenExceptionIsThrow() {
        //given
        User currentUser = UserUtil.getUser1();
        Item item = ItemUtil.getItem2_WhereOwnerUser2();

        int userId = 1;
        int bookingId = 2;

        Booking booking = Booking.builder()
                .id(bookingId)
                .booker(User.builder()
                        .id(100)
                        .build())
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .status(Status.WAITING)
                .build();

        Mockito.when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        //when
        UnauthorizedUser exception = assertThrows(UnauthorizedUser.class, () -> bookingService.findBooking(bookingId, userId));

        //then
        assertThat(exception.getMessage()).isEqualTo("Вы не можете просматривать данные этого бронирования");
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
        Mockito.when(bookingRepository.findByBookerIdOrderByStartDesc(anyInt(), any(Pageable.class))).thenReturn(List.of(booking));
        returnedBookings = bookingService.findBookingByCondition(userId, StateParam.ALL, Pageable.unpaged());

        //then
        assertThat(CollectionUtils.isEmpty(returnedBookings)).isFalse();

        //when
        Mockito.when(bookingRepository.findByBookerIdAndEndLessThanOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));
        returnedBookings = bookingService.findBookingByCondition(userId, StateParam.PAST, Pageable.unpaged());

        //then
        assertThat(CollectionUtils.isEmpty(returnedBookings)).isFalse();

        //when
        Mockito.when(bookingRepository.findByBookerIdAndStartGreaterThanOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));
        returnedBookings = bookingService.findBookingByCondition(userId, StateParam.FUTURE, Pageable.unpaged());

        //then
        assertThat(CollectionUtils.isEmpty(returnedBookings)).isFalse();

        //when
        Mockito.when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyInt(), any(Status.class), any(Pageable.class))).thenReturn(List.of(booking));
        returnedBookings = bookingService.findBookingByCondition(userId, StateParam.WAITING, Pageable.unpaged());

        //then
        assertThat(CollectionUtils.isEmpty(returnedBookings)).isFalse();

        //when
        Mockito.when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyInt(), any(Status.class), any(Pageable.class))).thenReturn(List.of(booking));
        returnedBookings = bookingService.findBookingByCondition(userId, StateParam.REJECTED, Pageable.unpaged());

        //then
        assertThat(CollectionUtils.isEmpty(returnedBookings)).isFalse();

        //when
        Mockito.when(bookingRepository.findCurrentBooking(any(LocalDateTime.class), anyInt(), any(Pageable.class))).thenReturn(List.of(booking));
        returnedBookings = bookingService.findBookingByCondition(userId, StateParam.CURRENT, Pageable.unpaged());

        //then
        assertThat(CollectionUtils.isEmpty(returnedBookings)).isFalse();
    }
}