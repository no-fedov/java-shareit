package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository("bookingRepository")
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdOrderByStartDesc(int bookerId);

    List<Booking> findByBookerIdAndEndLessThanOrderByStartDesc(int bookerId, LocalDateTime endDate);

    List<Booking> findByBookerIdAndStartGreaterThanOrderByStartDesc(int bookerId, LocalDateTime startDate);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(int bookerId, Status status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.start <= :time ")
    List<Booking> findByBookerIdAndItemIdAndStatusAndStartLessThan(@Param("bookerId") int bookerId,
                                                                   @Param("itemId") int itemId,
                                                                   @Param("status") Status status,
                                                                   @Param("time") LocalDateTime time);

    @Query(value = "select bk.* " +
            "from bookings as bk left join items as it on bk.item_id = it.id " +
            "where bk.start_date < ?1 and bk.end_date > ?1 " +
            "and bk.booker_id = ?2 " +
            "order by bk.id asc ", nativeQuery = true)
    List<Booking> findCurrentBooking(LocalDateTime date, int bookerId);

}
