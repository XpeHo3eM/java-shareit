package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i" +
            " FROM Item AS i" +
            " JOIN FETCH i.owner" +
            " WHERE i.owner.id = ?1")
    List<Item> findAllByOwnerId(long ownerId);

    @Query("SELECT i" +
            " FROM Item AS i" +
            " JOIN FETCH i.owner" +
            " WHERE i.id = ?1")
    Optional<Item> findByIdWithOwner(long id);

    @Query("SELECT i" +
            " FROM Item AS i" +
            " WHERE UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%'))" +
            "     OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%'))" +
            "     AND i.available = TRUE")
    List<Item> search(String text);
}
