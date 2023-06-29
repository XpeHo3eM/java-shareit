package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemDaoInMemory implements ItemDao {
    private static long uid = 0;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        item.setId(++uid);

        items.put(uid, item);

        log.debug("Item: {} added", item);

        return item;
    }

    @Override
    public List<Item> getAllItemsByOwnerId(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findItems(String search) {
        final String text = search.toUpperCase();

        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toUpperCase().contains(text)
                        || item.getDescription().toUpperCase().contains(text))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(long itemId) {
        return getItemOrThrowException(itemId);
    }

    @Override
    public Item updateItem(Item item) {
        Item itemInMemory = getItemOrThrowException(item.getId());

        log.debug("Item for update: {}, fields for update: {}", itemInMemory, item);

        if (item.getName() != null) {
            itemInMemory.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemInMemory.setDescription(item.getDescription());
        }
        itemInMemory.setAvailable(item.isAvailable());

        log.debug("Item after update: {}", itemInMemory);

        return itemInMemory;
    }

    @Override
    public void deleteItem(long itemId) {
        items.remove(itemId);

        log.debug("Item with id = {} deleted", itemId);
    }

    private Item getItemOrThrowException(long id) {
        if (!items.containsKey(id)) {
            throw new EntityNotFoundException(String.format("Вещь с ID = %d не найдена", id));
        }

        return items.get(id);
    }
}
