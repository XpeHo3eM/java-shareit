package ru.practicum.shareit.item.dal;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotAvailableBookingException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreatingCommentDto;
import ru.practicum.shareit.item.dto.item.CreatingItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Validator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto addItem(long userId, CreatingItemDto creatingItemDto) {
        User userInRepository = getUserOrThrowException(userId);

        Item item = ItemMapper.INSTANCE.toItem(creatingItemDto);
        item.setOwner(userInRepository);

        Long requestId = creatingItemDto.getRequestId();
        if (requestId != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Запрос с ID = %d не найден", requestId)));

            item.setItemRequest(itemRequest);
        }

        return ItemMapper.INSTANCE.toDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long userId, int from, int size) {
        Validator.validatePage(from, size);
        isUserExists(userId);

        PageRequest page = PageRequest.of(from / size, size);
        Page<Item> items = itemRepository.findAllByOwnerId(userId, page);

        if (!items.isEmpty() && items.getContent().get(0).getOwner().getId() == userId) {
            return items.stream()
                    .map(ItemMapper.INSTANCE::toDtoWithBooking)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public List<ItemDto> search(long userId, String text, int from, int size) {
        Validator.validatePage(from, size);
        isUserExists(userId);

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        PageRequest page = PageRequest.of(from / size, size);

        return itemRepository.search(text, page).stream()
                .map(ItemMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        getUserOrThrowException(userId);
        Item itemInRepository = getItemOrThrowException(itemId);

        if (itemInRepository.getOwner().getId() == userId) {
            return ItemMapper.INSTANCE.toDtoWithBooking(itemInRepository);
        }

        return ItemMapper.INSTANCE.toDto(itemInRepository);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, CreatingItemDto creatingItemDto) {
        User userInRepository = getUserOrThrowException(userId);
        Item itemInRepository = getItemOrThrowException(itemId);
        throwExceptionIfUserIsNotItemOwner(userInRepository, itemInRepository);
        Item item = ItemMapper.INSTANCE.toItem(creatingItemDto);

        if (item.getName() != null && !item.getName().isBlank()) {
            itemInRepository.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemInRepository.setDescription(item.getDescription());
        }
        if (creatingItemDto.getAvailable() != null) {
            itemInRepository.setAvailable(creatingItemDto.getAvailable());
        }

        return ItemMapper.INSTANCE.toDto(itemInRepository);
    }

    @Override
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

        Comment comment = CommentMapper.INSTANCE.toComment(creatingCommentDto);

        comment.setAuthor(userInRepository);
        comment.setItem(itemInRepository);
        comment.setCreationDate(LocalDateTime.now());

        return CommentMapper.INSTANCE.toDto(commentRepository.save(comment));
    }

    private User getUserOrThrowException(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с ID = %d не найден", id)));
    }

    private void isUserExists(long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Пользователь с ID = %d не найден", id));
        }
    }

    private Item getItemOrThrowException(long id) {
        return itemRepository.findByIdWithOwner(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещь с ID = %d не найдена", id)));
    }

    private void throwExceptionIfUserIsNotItemOwner(User user, Item itemOnStorage) {
        if (!itemOnStorage.getOwner().equals(user)) {
            throw new AccessDeniedException(String.format("Пользователь %s не является владельцем вещи %s",
                    user,
                    itemOnStorage));
        }
    }
}
