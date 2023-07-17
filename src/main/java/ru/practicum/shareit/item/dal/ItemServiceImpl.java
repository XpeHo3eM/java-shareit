package ru.practicum.shareit.item.dal;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotAvailableBookingException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreatingCommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.CreatingItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto addItem(long userId, CreatingItemDto creatingItemDto) {
        User userInRepository = getUserOrThrowException(userId);

        Item item = ItemMapper.toItem(creatingItemDto);
        item.setOwner(userInRepository);

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsByOwnerId(long userId) {
        getUserOrThrowException(userId);

        List<Item> items = itemRepository.findAllByOwnerId(userId);

        if (!items.isEmpty() && items.get(0).getOwner().getId() == userId) {
            return items.stream()
                    .map(ItemMapper::toDtoWithBooking)
                    .collect(Collectors.toList());
        }

        return items.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(long userId, String text) {
        getUserOrThrowException(userId);

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto getItemById(long userId, long itemId) {
        getUserOrThrowException(userId);
        Item itemInRepository = getItemOrThrowException(itemId);

        if (itemInRepository.getOwner().getId() == userId) {
            return ItemMapper.toDtoWithBooking(itemInRepository);
        }

        return ItemMapper.toDto(itemInRepository);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, CreatingItemDto creatingItemDto) {
        User userInRepository = getUserOrThrowException(userId);
        Item itemInRepository = getItemOrThrowException(itemId);
        throwExceptionIfUserIsNotItemOwner(userInRepository, itemInRepository);
        Item item = ItemMapper.toItem(creatingItemDto);

        if (item.getName() != null && !item.getName().isBlank()) {
            itemInRepository.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemInRepository.setDescription(item.getDescription());
        }
        if (creatingItemDto.getAvailable() != null) {
            itemInRepository.setAvailable(creatingItemDto.getAvailable());
        }

        return ItemMapper.toDto(itemInRepository);
    }

    @Override
    @Transactional
    public void deleteItem(long userId, long itemId) {
        User userInRepository = getUserOrThrowException(userId);
        Item itemInRepository = getItemOrThrowException(itemId);
        throwExceptionIfUserIsNotItemOwner(userInRepository, itemInRepository);

        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, CreatingCommentDto creatingCommentDto) {
        User userInRepository = getUserOrThrowException(userId);
        Item itemInRepository = getItemOrThrowException(itemId);

        boolean bookingByUser = itemInRepository.getBookings().stream()
                .anyMatch(b -> b.getBooker().getId() == userId
                        && b.getDateEnd().isBefore(LocalDateTime.now()));

        if (!bookingByUser) {
            throw new NotAvailableBookingException(String.format("Пользователь с ID = %s не брал в аренду %s", userId, itemInRepository));
        }

        Comment comment = CommentMapper.toComment(creatingCommentDto);

        comment.setAuthor(userInRepository);
        comment.setItem(itemInRepository);
        comment.setCreationDate(LocalDateTime.now());

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private User getUserOrThrowException(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с ID = %d не найден", id)));
    }

    private Item getItemOrThrowException(long id) {
        return itemRepository.findByIdWithOwner(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещь с ID = %d не найдена", id)));
    }

    private void throwExceptionIfUserIsNotItemOwner(User user, Item itemOnStorage) {
        if (itemOnStorage.getOwner().getId() != user.getId()) {
            throw new AccessDeniedException(String.format("Пользователь %s не является владельцем вещи %s",
                    user,
                    itemOnStorage));
        }
    }
}
