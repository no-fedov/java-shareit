package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.*;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

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
    @DisplayName("Test give bookings by booker id")
    public void givenTwoBooking_thenSaveTwoBooking_WhenReturnedTwoBookingByBookerId() {

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
                .item(ItemUtil.getItem3_WhereOwnerUser3())
                .status(Status.WAITING)
                .start(start)
                .end(end)
                .build();

        List<Booking> bookings = bookingRepository.saveAll(List.of(booking1, booking2));

        //when
        List<Booking> obtainedBookings = bookingRepository.findByBookerIdOrderByStartDesc(booker.getId());

        //then
        assertThat(CollectionUtils.isEmpty(obtainedBookings)).isFalse();
        assertThat(obtainedBookings.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test give bookings by booker id and end date less then end+2days")
    public void givenBooking_thenSaveTwoBooking_WhenReturnedBookingByBookerIdAndEndLessThen() {

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


        Booking booking = bookingRepository.save(booking1);

        //when
        List<Booking> obtainedBooking = bookingRepository.findByBookerIdAndEndLessThanOrderByStartDesc(booker.getId(), end.plusDays(2));

        //then
        assertThat(CollectionUtils.isEmpty(obtainedBooking)).isFalse();
        assertThat(obtainedBooking.size()).isEqualTo(1);
        assertThat(obtainedBooking.get(0)).isEqualTo(booking1);
    }

    @Test
    @DisplayName("Test give bookings by booker id and start date early then start -2days")
    public void givenBooking_thenSaveTwoBooking_WhenReturnedBookingByBookerIdAndStartEarly() {

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

        Booking booking = bookingRepository.save(booking1);

        //when
        List<Booking> obtainedBooking = bookingRepository.findByBookerIdAndStartGreaterThanOrderByStartDesc(booker.getId(), start.minusDays(2));

        //then
        assertThat(CollectionUtils.isEmpty(obtainedBooking)).isFalse();
        assertThat(obtainedBooking.size()).isEqualTo(1);
        assertThat(obtainedBooking.get(0)).isEqualTo(booking1);
    }

    @Test
    @DisplayName("Test give bookings by booker id and status")
    public void givenBooking_thenSaveTwoBooking_WhenReturnedBookingByBookerIdAndStatus() {

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

        Booking booking = bookingRepository.save(booking1);

        //when
        List<Booking> obtainedBooking = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.WAITING);

        //then
        assertThat(CollectionUtils.isEmpty(obtainedBooking)).isFalse();
        assertThat(obtainedBooking.size()).isEqualTo(1);
        assertThat(obtainedBooking.get(0)).isEqualTo(booking1);
    }

    @Test
    @DisplayName("Test give bookings by booker id and status and start early then now + 1 day")
    public void givenBooking_thenSaveTwoBooking_WhenReturnedBookingByBookerIdAndStatusAndStartEarly() {

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

        Booking booking = bookingRepository.save(booking1);

        //when
        List<Booking> obtainedBooking = bookingRepository.findByBookerIdAndItemIdAndStatusAndStartLessThan(booker.getId(),
                ItemUtil.getItem2_WhereOwnerUser2().getId(),
                Status.WAITING,
                start.plusDays(2)
        );

        //then
        assertThat(CollectionUtils.isEmpty(obtainedBooking)).isFalse();
        assertThat(obtainedBooking.size()).isEqualTo(1);
        assertThat(obtainedBooking.get(0)).isEqualTo(booking1);
    }

    @Test
    @DisplayName("Test give current bookings by booker id and status and start early then now + 1 day")
    public void givenBooking_thenSaveTwoBooking_WhenReturnedCurrentBooking() {

        //given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);

        User booker = UserUtil.getUser1();

        Booking booking1 = Booking.builder()
                .booker(booker)
                .item(ItemUtil.getItem2_WhereOwnerUser2())
                .status(Status.APPROVED)
                .start(start)
                .end(end)
                .build();

        Booking booking = bookingRepository.save(booking1);

        //when
        List<Booking> obtainedBooking = bookingRepository.findCurrentBooking(start.plusDays(1),
                booker.getId());

        //then
        assertThat(CollectionUtils.isEmpty(obtainedBooking)).isFalse();
        assertThat(obtainedBooking.size()).isEqualTo(1);
        assertThat(obtainedBooking.get(0)).isEqualTo(booking1);
    }


}
