package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.CollectionUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.ItemUtil;
import ru.practicum.shareit.util.UserUtil;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = "/schema-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BookingOwnerTests {
    @Autowired
    private BookingOwnerRepository bookingOwnerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        userRepository.saveAll(List.of(UserUtil.getUser1(),
                UserUtil.getUser2(),
                UserUtil.getUser3()));

        itemRepository.saveAll(List.of(ItemUtil.getItem1_WhereOwnerUser1(),
                ItemUtil.getItem2_WhereOwnerUser2(),
                ItemUtil.getItem3_WhereOwnerUser3()));
    }

    @Test
    @DisplayName("Test give bookings by owner id of item")
    public void givenBooking_whenSaveBooking_thenReturnBookingsByOwnerId() {
        //given
        User ownerItem = UserUtil.getUser2();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);

        User booker = UserUtil.getUser1();

        Booking booking1 = Booking.builder()
                .booker(booker)
                .item(ItemUtil.getItem2_WhereOwnerUser2())
                .status(Status.WAITING)
                .start(start)
                .end(end)
                .build();

        //when
        Booking obtainedBooking = bookingOwnerRepository.save(booking1);

        //then
        List<Booking> bookings = bookingOwnerRepository.findAllByItemOwnerIdOrderByStartDesc(ownerItem.getId());
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0)).isEqualTo(booking1);
    }

    @Test
    @DisplayName("Test give bookings by owner id of item and end date < time")
    public void givenBooking_whenSaveBooking_thenReturnBookingsByOwnerIdAndEndDateLessThenTime() {
        //given
        User ownerItem = UserUtil.getUser2();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);

        User booker = UserUtil.getUser1();

        Booking booking1 = Booking.builder()
                .booker(booker)
                .item(ItemUtil.getItem2_WhereOwnerUser2())
                .status(Status.WAITING)
                .start(start)
                .end(end)
                .build();

        //when
        Booking obtainedBooking = bookingOwnerRepository.save(booking1);

        //then
        List<Booking> bookings = bookingOwnerRepository.findAllByItemOwnerIdAndEndLessThanOrderByStartDesc(ownerItem.getId(), end.plusDays(1));
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0)).isEqualTo(booking1);
    }

    @Test
    @DisplayName("Test give bookings by owner id of item and start date < time")
    public void givenBooking_whenSaveBooking_thenReturnBookingsByOwnerIdAndStarDateLessThenTime() {
        //given
        User ownerItem = UserUtil.getUser2();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);

        User booker = UserUtil.getUser1();

        Booking booking1 = Booking.builder()
                .booker(booker)
                .item(ItemUtil.getItem2_WhereOwnerUser2())
                .status(Status.WAITING)
                .start(start)
                .end(end)
                .build();

        //when
        Booking obtainedBooking = bookingOwnerRepository.save(booking1);

        //then
        List<Booking> bookings = bookingOwnerRepository.findAllByItemOwnerIdAndStartGreaterThanOrderByStartDesc(ownerItem.getId(), start.minusDays(1));
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0)).isEqualTo(booking1);
    }

    @Test
    @DisplayName("Test give bookings by owner id of item and status = WAITING")
    public void givenBooking_whenSaveBooking_thenReturnBookingsByOwnerIdAndStatusWaiting() {
        //given
        User ownerItem = UserUtil.getUser2();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);

        User booker = UserUtil.getUser1();

        Booking booking1 = Booking.builder()
                .booker(booker)
                .item(ItemUtil.getItem2_WhereOwnerUser2())
                .status(Status.WAITING)
                .start(start)
                .end(end)
                .build();

        //when
        Booking obtainedBooking = bookingOwnerRepository.save(booking1);

        //then
        List<Booking> bookings = bookingOwnerRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerItem.getId(), Status.WAITING);
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0)).isEqualTo(booking1);
    }

    @Test
    @DisplayName("Test give previous booking")
    public void givenTwoBookingsOfOneItem_whenGetPreviousBooking_thenReturnPreviousBooking() {
        //given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);

        User booker = UserUtil.getUser1();

        Booking booking1 = Booking.builder()
                .booker(booker)
                .item(ItemUtil.getItem2_WhereOwnerUser2())
                .status(Status.REJECTED)
                .start(start)
                .end(end)
                .build();

        Booking booking2 = Booking.builder()
                .booker(booker)
                .item(ItemUtil.getItem2_WhereOwnerUser2())
                .status(Status.REJECTED)
                .start(end.plusDays(1))
                .end(end.plusDays(2))
                .build();

        bookingOwnerRepository.saveAll(List.of(booking1,booking2));

        //when
        Booking booking = bookingOwnerRepository.findPreviousBooking(ItemUtil.getItem2_WhereOwnerUser2().getId(), end.plusDays(3));

        //then
        assertThat(booking).isNotNull();
        assertThat(booking).isEqualTo(booking2);
    }

    @Test
    @DisplayName("Test give next booking")
    public void givenTwoBookingsOfOneItem_whenGetNextBooking_thenReturnNextBooking() {
        //given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);

        User booker = UserUtil.getUser1();

        Booking booking1 = Booking.builder()
                .booker(booker)
                .item(ItemUtil.getItem2_WhereOwnerUser2())
                .status(Status.WAITING)
                .start(start)
                .end(end)
                .build();

        Booking booking2 = Booking.builder()
                .booker(booker)
                .item(ItemUtil.getItem2_WhereOwnerUser2())
                .status(Status.WAITING)
                .start(end.plusDays(1))
                .end(end.plusDays(2))
                .build();

        bookingOwnerRepository.saveAll(List.of(booking1,booking2));

        //when
        Booking booking = bookingOwnerRepository.findNextBooking(ItemUtil.getItem2_WhereOwnerUser2().getId(), end);

        //then
        assertThat(booking).isNotNull();
        assertThat(booking).isEqualTo(booking2);
    }

    @Test
    @DisplayName("Test give current bookings")
    public void givenTwoBookingsOfOneItem_whenGetNextBooking_thenReturnCurrentTwoBooking() {
        //given
        LocalDateTime start = LocalDateTime.of(2023, 12, 1,1,1);
        LocalDateTime end = start.plusDays(2);

        User booker = UserUtil.getUser1();
        User owner = UserUtil.getUser2();

        Booking booking1 = Booking.builder()
                .booker(booker)
                .item(ItemUtil.getItem2_WhereOwnerUser2())
                .status(Status.APPROVED)
                .start(start)
                .end(end)
                .build();

        Booking booking2 = Booking.builder()
                .booker(booker)
                .item(ItemUtil.getItem2_WhereOwnerUser2())
                .status(Status.APPROVED)
                .start(end.plusDays(1))
                .end(end.plusDays(2))
                .build();

        bookingOwnerRepository.saveAll(List.of(booking1,booking2));

        //when
        List<Booking> bookings = bookingOwnerRepository.findCurrentBooking(owner.getId(), start);

        //then
        assertThat(CollectionUtils.isEmpty(bookings)).isFalse();
        assertThat(bookings.size()).isEqualTo(1);
    }
}
