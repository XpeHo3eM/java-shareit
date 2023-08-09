package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT i" +
            " FROM Item AS i" +
            " JOIN FETCH i.owner AS o" +
            " LEFT JOIN FETCH i.itemRequest" +
            " LEFT JOIN FETCH i.comments" +
            " LEFT JOIN FETCH i.bookings" +
            " WHERE o.id = :id",
            countQuery = "SELECT COUNT(i)" +
                    " FROM Item i" +
                    " WHERE i.owner.id = :id")
    Page<Item> findAllByOwnerId(@Param("id") Long ownerId, Pageable page);

    @Query("SELECT i" +
            " FROM Item AS i" +
            " JOIN FETCH i.owner" +
            " LEFT JOIN FETCH i.itemRequest" +
            " LEFT JOIN FETCH i.comments" +
            " LEFT JOIN FETCH i.bookings" +
            " WHERE i.id = :id")
    Optional<Item> findByIdWithOwner(@Param("id") Long id);

    @Query(value = "SELECT i" +
            " FROM Item AS i" +
            " JOIN FETCH i.owner" +
            " LEFT JOIN FETCH i.itemRequest" +
            " LEFT JOIN FETCH i.comments" +
            " LEFT JOIN FETCH i.bookings" +
            " WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', :text, '%'))" +
            "     OR UPPER(i.description) LIKE UPPER(CONCAT('%', :text, '%')))" +
            "     AND i.available = TRUE",
            countQuery = "SELECT COUNT(i)" +
                    " FROM Item AS i" +
                    " WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', :text, '%'))" +
                    "     OR UPPER(i.description) LIKE UPPER(CONCAT('%', :text, '%')))" +
                    "     AND i.available = TRUE")
    Page<Item> search(@Param("text") String text, Pageable page);

    List<Item> findItemByItemRequestIn(List<ItemRequest> requests);
}
