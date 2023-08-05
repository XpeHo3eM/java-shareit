package ru.practicum.shareit.request.dal;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.util.Validator;

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

    @Override
    @Transactional
    public ItemRequestDto addItemRequest(long userId, CreatingItemRequestDto creatingItemRequestDto) {
        User user = getUserOrThrowException(userId);

        ItemRequest itemRequest = ItemRequestMapper.INSTANCE.toItemRequest(creatingItemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.INSTANCE.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getOwnerItemRequests(long userId) {
        User user = getUserOrThrowException(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequester(user);
        Map<ItemRequest, List<Item>> items = getItemsForItemRequest(itemRequests);

        return itemRequests.stream()
                .map(itemRequest -> {
                    ItemRequestDto itemRequestDto = ItemRequestMapper.INSTANCE.toItemRequestDto(itemRequest);
                    itemRequestDto.setItems(getItems(itemRequest, items));

                    return itemRequestDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(long userId, int from, int size) {
        Validator.validatePage(from, size);
        User user = getUserOrThrowException(userId);

        PageRequest page = PageRequest.of(from / size, size, Sort.by("created").descending());

        Page<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterNot(user, page);
        Map<ItemRequest, List<Item>> items = getItemsForItemRequest(itemRequests.getContent());

        return itemRequests.stream()
                .map(itemRequest -> {
                    ItemRequestDto itemRequestDto = ItemRequestMapper.INSTANCE.toItemRequestDto(itemRequest);
                    itemRequestDto.setItems(getItems(itemRequest, items));

                    return itemRequestDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequest(long userId, long requestId) {
        getUserOrThrowException(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Запрос с ID = %d не найден", requestId)));

        Map<ItemRequest, List<Item>> items = getItemsForItemRequest(List.of(itemRequest));
        ItemRequestDto itemRequestDto = ItemRequestMapper.INSTANCE.toItemRequestDto(itemRequest);
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

    private List<ItemRequestReplyDto> getItems(ItemRequest itemRequest,  Map<ItemRequest, List<Item>> items) {
        return items.getOrDefault(itemRequest, Collections.emptyList()).stream()
                .map(ItemRequestMapper.INSTANCE::toItemRequestReplyDto)
                .collect(Collectors.toList());
    }
}
