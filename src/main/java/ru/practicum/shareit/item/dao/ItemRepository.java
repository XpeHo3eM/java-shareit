package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i" +
            " FROM Item AS i" +
            " JOIN FETCH i.owner" +
            " WHERE i.owner.id = :id")
    List<Item> findAllByOwnerId(@Param("id") Long ownerId);

    @Query("SELECT i" +
            " FROM Item AS i" +
            " JOIN FETCH i.owner" +
            " WHERE i.id = :id")
    Optional<Item> findByIdWithOwner(@Param("id") Long id);

    @Query("SELECT i" +
            " FROM Item AS i" +
            " WHERE UPPER(i.name) LIKE UPPER(CONCAT('%', :text, '%'))" +
            "     OR UPPER(i.description) LIKE UPPER(CONCAT('%', :text, '%'))" +
            "     AND i.available = TRUE")
    List<Item> search(@Param("text") String text);
}
