package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
            " WHERE b.booker = ?1" +
            "     AND b.dateEnd < ?2" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByBookerPast(User booker, LocalDateTime now);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = ?1" +
            "     AND b.dateStart > ?2" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByBookerFuture(User booker, LocalDateTime now);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = ?1" +
            "     AND b.dateStart < ?2" +
            "     AND b.dateEnd > ?2" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByBookerCurrent(User booker, LocalDateTime now);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = ?1" +
            "     AND b.status = ?2" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByBookerAndStatus(User booker, StatusType statusType);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item" +
            " JOIN FETCH b.booker" +
            " WHERE b.booker = ?1" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByBooker(User booker);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = ?1" +
            "     AND b.dateEnd < ?2" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByOwnerPast(User owner, LocalDateTime now);

    @Query("SELECT b " +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = ?1" +
            "     AND b.dateStart > ?2" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByOwnerFuture(User owner, LocalDateTime now);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = ?1" +
            "     AND b.dateStart < ?2" +
            "     AND b.dateEnd > ?2" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByOwnerCurrent(User owner, LocalDateTime now);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = ?1" +
            "     AND b.status = ?2" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByOwnerAndStatus(User owner, StatusType statusType);

    @Query("SELECT b" +
            " FROM Booking AS b" +
            " JOIN FETCH b.item AS i" +
            " JOIN FETCH b.booker" +
            " WHERE i.owner = ?1" +
            " ORDER BY b.dateStart DESC")
    List<Booking> findAllByOwner(User owner);
}
