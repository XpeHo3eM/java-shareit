package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("SELECT i" +
            " FROM ItemRequest AS i" +
            " JOIN FETCH i.requester" +
            " WHERE i.requester = :user" +
            " ORDER BY i.created DESC")
    List<ItemRequest> findAllByRequester(@Param("user") User owner);

    @Query(value = "SELECT i" +
            " FROM ItemRequest AS i" +
            " JOIN FETCH i.requester" +
            " WHERE i.requester != :user",
            countQuery = "SELECT COUNT(i)" +
                    " FROM ItemRequest AS i" +
                    " WHERE i.requester != :user")
    Page<ItemRequest> findAllByRequesterNot(@Param("user") User user, Pageable page);
}
