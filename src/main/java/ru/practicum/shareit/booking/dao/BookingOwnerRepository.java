package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository("ownerBookingRepository")
public interface BookingOwnerRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(int ownerId);

    List<Booking> findAllByItemOwnerIdAndEndLessThanOrderByStartDesc(int ownerId, LocalDateTime endDate);

    List<Booking> findAllByItemOwnerIdAndStartGreaterThanOrderByStartDesc(int ownerId, LocalDateTime startDate);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(int ownerId, Status status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.start <= :time")
    List<Booking> findByBookerIdAndItemIdAndStatusAndStartLessThan(@Param("bookerId") int bookerId,
                                                                   @Param("itemId") int itemId,
                                                                   @Param("status") Status status,
                                                                   @Param("time") LocalDateTime time);

    @Query(value = "select bk.* " +
            "from bookings as bk left join items as it on bk.item_id = it.id " +
            "where bk.item_id = ?1 and " +
            "((bk.end_date < ?2) OR (bk.start_date <= ?2 and bk.end_date >= ?2))" +
            "order by bk.end_date desc " +
            "limit 1", nativeQuery = true)
    Booking findPreviousBooking(int itemId, LocalDateTime date);

    @Query(value = "select bk.* " +
            "from bookings as bk left join items as it on bk.item_id = it.id " +
            "where bk.item_id = ?1 and bk.start_date > ?2 " +
            "and bk.status != 'REJECTED' " +
            "order by bk.start_date asc " +
            "limit 1", nativeQuery = true)
    Booking findNextBooking(int itemId, LocalDateTime date);

    @Query(value = "select bk.* " +
            "from bookings as bk left join items as it on bk.item_id = it.id " +
            "where bk.start_date < ?1 and bk.end_date > ?1 and bk.status != 'APPROVED' order by bk.id asc ", nativeQuery = true)
    List<Booking> findCurrentBooking(LocalDateTime time);
}