package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = :user" +
            "     AND b.dateEnd < :time" +
            " ORDER BY b.dateStart DESC")
    Page<Booking> findAllByBookerPast(@Param("user") User booker, @Param("time") LocalDateTime now, Pageable page);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = :user" +
            "     AND b.dateStart > :time" +
            " ORDER BY b.dateStart DESC")
    Page<Booking> findAllByBookerFuture(@Param("user") User booker, @Param("time") LocalDateTime now, Pageable page);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = :user" +
            "     AND b.dateStart < :time" +
            "     AND b.dateEnd > :time" +
            " ORDER BY b.dateStart DESC")
    Page<Booking> findAllByBookerCurrent(@Param("user") User booker, @Param("time") LocalDateTime now, Pageable page);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = :user" +
            "     AND b.status = :status" +
            " ORDER BY b.dateStart DESC")
    Page<Booking> findAllByBookerAndStatus(@Param("user") User booker, @Param("status") StatusType statusType, Pageable page);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = :user" +
            " ORDER BY b.dateStart DESC")
    Page<Booking> findAllByBooker(@Param("user") User booker, Pageable page);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = :user" +
            "     AND b.dateEnd < :time" +
            " ORDER BY b.dateStart DESC")
    Page<Booking> findAllByOwnerPast(@Param("user") User owner, @Param("time") LocalDateTime now, Pageable page);

    @Query("SELECT b " +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = :user" +
            "     AND b.dateStart > :time" +
            " ORDER BY b.dateStart DESC")
    Page<Booking> findAllByOwnerFuture(@Param("user") User owner, @Param("time") LocalDateTime now, Pageable page);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = :user" +
            "     AND b.dateStart < :time" +
            "     AND b.dateEnd > :time" +
            " ORDER BY b.dateStart DESC")
    Page<Booking> findAllByOwnerCurrent(@Param("user") User owner, @Param("time") LocalDateTime now, Pageable page);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = :user" +
            "     AND b.status = :status" +
            " ORDER BY b.dateStart DESC")
    Page<Booking> findAllByOwnerAndStatus(@Param("user") User owner, @Param("status") StatusType statusType, Pageable page);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = :user" +
            " ORDER BY b.dateStart DESC")
    Page<Booking> findAllByOwner(@Param("user") User owner, Pageable page);
}
