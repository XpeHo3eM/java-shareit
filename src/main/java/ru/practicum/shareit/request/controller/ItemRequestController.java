package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dal.ItemRequestService;
import ru.practicum.shareit.request.dto.CreatingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.Constant;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.util.Constant.HEADER_USER_ID;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(HEADER_USER_ID) long userId,
                                         @Valid @RequestBody CreatingItemRequestDto creatingItemRequestDto) {
        return service.addItemRequest(userId, creatingItemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequests(@RequestHeader(HEADER_USER_ID) long userId) {
        return service.getOwnerItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(HEADER_USER_ID) long userId,
                                                   @RequestParam(defaultValue = Constant.DEFAULT_START_PAGE) Integer from,
                                                   @RequestParam(defaultValue = Constant.DEFAULT_SIZE_PAGE) Integer size) {
        return service.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader(HEADER_USER_ID) long userId,
                                         @PathVariable long requestId) {
        return service.getItemRequest(userId, requestId);
    }
}
