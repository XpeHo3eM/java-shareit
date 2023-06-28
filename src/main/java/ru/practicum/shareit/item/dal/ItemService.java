package ru.practicum.shareit.item.dal;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long userId, ItemDto itemDto);

    List<ItemDto> getAllItemsByOwnerId(long userId);

    List<ItemDto> findAllAvailableItems(long userId, String search);

    ItemDto getItemById(long userId, long itemId);

    ItemDto updateItem(long userId, ItemDto itemDto);

    void deleteItem(long userId, long itemId);
}
