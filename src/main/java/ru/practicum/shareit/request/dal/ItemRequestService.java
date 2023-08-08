package ru.practicum.shareit.request.dal;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.CreatingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(long userId, CreatingItemRequestDto creatingItemRequestDto);

    List<ItemRequestDto> getOwnerItemRequests(long userId);

    List<ItemRequestDto> getAllItemRequests(long userId, Pageable pageable);

    ItemRequestDto getItemRequest(long userId, long requestId);
}
