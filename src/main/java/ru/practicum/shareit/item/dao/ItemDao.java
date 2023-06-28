package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item addItem(Item item);

    List<Item> getAllItemsByOwnerId(long userId);

    List<Item> findItems(String search);

    Item getItemById(long itemId);

    Item updateItem(Item item);

    void deleteItem(long itemId);
}
