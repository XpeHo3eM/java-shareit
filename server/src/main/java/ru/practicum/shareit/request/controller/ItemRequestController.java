package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dal.ItemRequestService;
import ru.practicum.shareit.request.dto.CreatingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static ru.practicum.shareit.util.Constant.HEADER_USER_ID;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(HEADER_USER_ID) long userId,
                                         @RequestBody CreatingItemRequestDto creatingItemRequestDto) {
        return service.addItemRequest(userId, creatingItemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequests(@RequestHeader(HEADER_USER_ID) long userId) {
        return service.getOwnerItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(HEADER_USER_ID) long userId,
                                                   @RequestParam Integer from,
                                                   @RequestParam Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by("created").descending());

        return service.getAllItemRequests(userId, page);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader(HEADER_USER_ID) long userId,
                                         @PathVariable long requestId) {
        return service.getItemRequest(userId, requestId);
    }
}
