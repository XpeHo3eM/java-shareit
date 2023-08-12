package ru.practicum.shareit.item.dal;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreatingCommentDto;
import ru.practicum.shareit.item.dto.item.CreatingItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long userId, CreatingItemDto creatingItemDto);

    List<ItemDto> getAllItemsByUserId(long userId, Pageable pageable);

    List<ItemDto> search(long userId, String search, Pageable pageable);

    ItemDto getItemById(long userId, long itemId);

    ItemDto updateItem(long userId, long itemId, CreatingItemDto creatingItemDto);

    void deleteItem(long userId, long itemId);

    CommentDto addComment(long userId, long itemId, CreatingCommentDto creatingCommentDto);
}
