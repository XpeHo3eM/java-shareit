package ru.practicum.shareit.item.dal;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.util.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemStorage;
    private final UserDao userStorage;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        throwExceptionIfUserNotExists(userId);

        Item item = Mapper.toItem(itemDto);
        item.setOwnerId(userId);

        return Mapper.itemToDto(itemStorage.addItem(item));
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(long userId) {
        throwExceptionIfUserNotExists(userId);

        return itemStorage.getAllItemsByOwnerId(userId).stream()
                .map(Mapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findAllAvailableItems(long userId, String search) {
        throwExceptionIfUserNotExists(userId);

        if (search.isBlank()) {
            return Collections.emptyList();
        }

        return itemStorage.findItems(search).stream()
                .map(Mapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        throwExceptionIfUserNotExists(userId);

        return Mapper.itemToDto(itemStorage.getItemById(itemId));
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto) {
        throwExceptionIfUserNotExists(userId);

        Item itemOnStorage = itemStorage.getItemById(itemDto.getId());
        checkIsUserItemOwner(userId, itemOnStorage);

        Item item = Mapper.toItem(itemDto);

        boolean available = itemDto.getAvailable() != null ? itemDto.getAvailable() : itemOnStorage.isAvailable();
        item.setAvailable(available);

        return Mapper.itemToDto(itemStorage.updateItem(item));
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        throwExceptionIfUserNotExists(userId);

        Item itemOnStorage = itemStorage.getItemById(itemId);
        checkIsUserItemOwner(userId, itemOnStorage);

        itemStorage.deleteItem(itemId);
    }

    private void throwExceptionIfUserNotExists(long id) {
        userStorage.getUserById(id);
    }

    private void checkIsUserItemOwner(long userId, Item itemOnStorage) {
        if (itemOnStorage.getOwnerId() != userId) {
            throw new AccessDeniedException(String.format("Пользователь %s не является владельцем вещи %s",
                    userStorage.getUserById(userId),
                    itemOnStorage));
        }
    }
}
