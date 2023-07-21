package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = :user" +
            "     AND b.dateEnd < :time" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByBookerPast(@Param("user") User booker, @Param("time") LocalDateTime now);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = :user" +
            "     AND b.dateStart > :time" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByBookerFuture(@Param("user") User booker, @Param("time") LocalDateTime now);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = :user" +
            "     AND b.dateStart < :time" +
            "     AND b.dateEnd > :time" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByBookerCurrent(@Param("user") User booker, @Param("time") LocalDateTime now);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = :user" +
            "     AND b.status = :status" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByBookerAndStatus(@Param("user") User booker, @Param("status") StatusType statusType);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = :user" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByBooker(@Param("user") User booker);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = :user" +
            "     AND b.dateEnd < :time" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByOwnerPast(@Param("user") User owner, @Param("time") LocalDateTime now);

    @Query("SELECT b " +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = :user" +
            "     AND b.dateStart > :time" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByOwnerFuture(@Param("user") User owner, @Param("time") LocalDateTime now);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = :user" +
            "     AND b.dateStart < :time" +
            "     AND b.dateEnd > :time" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByOwnerCurrent(@Param("user") User owner, @Param("time") LocalDateTime now);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = :user" +
            "     AND b.status = :status" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByOwnerAndStatus(@Param("user") User owner, @Param("status") StatusType statusType);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = :user" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByOwner(@Param("user") User owner);
}
