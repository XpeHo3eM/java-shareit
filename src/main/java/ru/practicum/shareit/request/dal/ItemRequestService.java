package ru.practicum.shareit.request.dal;

import ru.practicum.shareit.request.dto.CreatingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(long userId, CreatingItemRequestDto creatingItemRequestDto);

    List<ItemRequestDto> getOwnerItemRequests(long userId);

    List<ItemRequestDto> getAllItemRequests(long userId, int from, int size);

    ItemRequestDto getItemRequest(long userId, long requestId);
}
