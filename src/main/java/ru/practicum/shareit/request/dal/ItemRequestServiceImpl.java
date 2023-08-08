package ru.practicum.shareit.request.dal;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.CreatingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestReplyDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemRequestRepository itemRequestRepository;
    private ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequestDto addItemRequest(long userId, CreatingItemRequestDto creatingItemRequestDto) {
        User user = getUserOrThrowException(userId);

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(creatingItemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getOwnerItemRequests(long userId) {
        User user = getUserOrThrowException(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequester(user);
        Map<ItemRequest, List<Item>> items = getItemsForItemRequest(itemRequests);

        return itemRequests.stream()
                .map(itemRequest -> {
                    ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
                    itemRequestDto.setItems(getItems(itemRequest, items));

                    return itemRequestDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(long userId, Pageable pageable) {
        User user = getUserOrThrowException(userId);

        Page<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterNot(user, pageable);
        Map<ItemRequest, List<Item>> items = getItemsForItemRequest(itemRequests.getContent());

        return itemRequests.stream()
                .map(itemRequest -> {
                    ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
                    itemRequestDto.setItems(getItems(itemRequest, items));

                    return itemRequestDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequest(long userId, long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с ID = %d не найден", userId));
        }

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Запрос с ID = %d не найден", requestId)));

        Map<ItemRequest, List<Item>> items = getItemsForItemRequest(List.of(itemRequest));
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(getItems(itemRequest, items));

        return itemRequestDto;
    }

    private User getUserOrThrowException(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с ID = %d не найден", id)));
    }

    private Map<ItemRequest, List<Item>> getItemsForItemRequest(List<ItemRequest> itemRequests) {
        return itemRepository.findItemByItemRequestIn(itemRequests).stream()
                .collect(Collectors.groupingBy(Item::getItemRequest));
    }

    private List<ItemRequestReplyDto> getItems(ItemRequest itemRequest, Map<ItemRequest, List<Item>> items) {
        return items.getOrDefault(itemRequest, Collections.emptyList()).stream()
                .map(itemRequestMapper::toItemRequestReplyDto)
                .collect(Collectors.toList());
    }
}
