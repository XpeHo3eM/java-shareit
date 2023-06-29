package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dal.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.Constant;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private ItemService service;

    @PostMapping
    public ItemDto addItem(@RequestHeader(Constant.HEADER_USER_ID) long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        log.debug("AddItem userId: {}, itemDto: {}", userId, itemDto);

        return service.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(Constant.HEADER_USER_ID) long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        log.debug("PatchItem userId: {}, itemId: {}, body: {}", userId, itemId, itemDto);

        itemDto.setId(itemId);

        return service.updateItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(Constant.HEADER_USER_ID) long userId,
                           @PathVariable long itemId) {
        log.debug("GetItem userId: {}, itemId: {}", userId, itemId);

        return service.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader(Constant.HEADER_USER_ID) long userId) {
        log.debug("GetOwnerItems userId: {}", userId);

        return service.getAllItemsByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItem(@RequestHeader(Constant.HEADER_USER_ID) long userId,
                                  @RequestParam String text) {
        log.debug("FindItem userId: {}, find: {}", userId, text);

        return service.findAllAvailableItems(userId, text);
    }
}
