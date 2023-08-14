package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = :user" +
            "     AND b.dateEnd < :time",
            countQuery = "SELECT COUNT(b)" +
                    " FROM Booking AS b" +
                    " WHERE b.booker = :user" +
                    "     AND b.dateEnd < :time")
    Page<Booking> findAllByBookerAndPast(@Param("user") User booker, @Param("time") LocalDateTime now, Pageable page);

    @Query(value = "SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = :user" +
            "     AND b.dateStart > :time",
            countQuery = "SELECT COUNT(b)" +
                    " FROM Booking AS b" +
                    " WHERE b.booker = :user" +
                    "     AND b.dateStart > :time")
    Page<Booking> findAllByBookerAndFuture(@Param("user") User booker, @Param("time") LocalDateTime now, Pageable page);

    @Query(value = "SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = :user" +
            "     AND b.dateStart < :time" +
            "     AND b.dateEnd > :time",
            countQuery = "SELECT COUNT(b)" +
                    " FROM Booking AS b" +
                    " WHERE b.booker = :user" +
                    "     AND b.dateStart < :time" +
                    "     AND b.dateEnd > :time")
    Page<Booking> findAllByBookerAndCurrent(@Param("user") User booker, @Param("time") LocalDateTime now, Pageable page);

    @Query(value = "SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = :user" +
            "     AND b.status = :status",
            countQuery = "SELECT COUNT(b)" +
                    " FROM Booking AS b" +
                    " WHERE b.booker = :user" +
                    "     AND b.status = :status")
    Page<Booking> findAllByBookerAndStatus(@Param("user") User booker, @Param("status") StatusType statusType, Pageable page);

    @Query(value = "SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = :user",
            countQuery = "SELECT COUNT(b)" +
                    " FROM Booking AS b" +
                    " WHERE b.booker = :user")
    Page<Booking> findAllByBooker(@Param("user") User booker, Pageable page);

    @Query(value = "SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = :user" +
            "     AND b.dateEnd < :time",
            countQuery = "SELECT COUNT(b)" +
                    " FROM Booking AS b" +
                    " WHERE b.item.owner = :user" +
                    "     AND b.dateEnd < :time")
    Page<Booking> findAllByOwnerAndPast(@Param("user") User owner, @Param("time") LocalDateTime now, Pageable page);

    @Query(value = "SELECT b " +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = :user" +
            "     AND b.dateStart > :time",
            countQuery = "SELECT COUNT(b)" +
                    " FROM Booking AS b" +
                    " WHERE b.item.owner = :user" +
                    "     AND b.dateStart > :time")
    Page<Booking> findAllByOwnerAndFuture(@Param("user") User owner, @Param("time") LocalDateTime now, Pageable page);

    @Query(value = "SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = :user" +
            "     AND b.dateStart < :time" +
            "     AND b.dateEnd > :time",
            countQuery = "SELECT COUNT(b)" +
                    " FROM Booking AS b" +
                    " WHERE b.item.owner = :user" +
                    "     AND b.dateStart < :time" +
                    "     AND b.dateEnd > :time")
    Page<Booking> findAllByOwnerAndCurrent(@Param("user") User owner, @Param("time") LocalDateTime now, Pageable page);

    @Query(value = "SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = :user" +
            "     AND b.status = :status",
            countQuery = "SELECT COUNT(b)" +
                    " FROM Booking AS b" +
                    " WHERE b.item.owner = :user" +
                    "     AND b.status = :status")
    Page<Booking> findAllByOwnerAndStatus(@Param("user") User owner, @Param("status") StatusType statusType, Pageable page);

    @Query(value = "SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = :user",
            countQuery = "SELECT COUNT(b)" +
                    " FROM Booking AS b" +
                    " WHERE b.item.owner = :user")
    Page<Booking> findAllByOwner(@Param("user") User owner, Pageable page);
}
