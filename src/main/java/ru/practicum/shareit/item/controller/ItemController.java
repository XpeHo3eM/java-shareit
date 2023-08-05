package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dal.ItemService;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreatingCommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.CreatingItemDto;
import ru.practicum.shareit.util.Constant;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.util.Constant.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto addItem(@RequestHeader(HEADER_USER_ID) long userId,
                           @Valid @RequestBody CreatingItemDto creatingItemDto) {
        return service.addItem(userId, creatingItemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER_USER_ID) long userId,
                              @PathVariable long itemId,
                              @RequestBody CreatingItemDto creatingItemDto) {
        return service.updateItem(userId, itemId, creatingItemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(HEADER_USER_ID) long userId,
                           @PathVariable long itemId) {
        return service.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader(HEADER_USER_ID) long userId,
                                       @RequestParam(defaultValue = DEFAULT_START_PAGE) Integer from,
                                       @RequestParam(defaultValue = DEFAULT_SIZE_PAGE) Integer size) {
        return service.getAllItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> findItem(@RequestHeader(HEADER_USER_ID) long userId,
                                  @RequestParam String text,
                                  @RequestParam(defaultValue = DEFAULT_START_PAGE) Integer from,
                                  @RequestParam(defaultValue = DEFAULT_SIZE_PAGE) Integer size) {
        return service.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(HEADER_USER_ID) long userId,
                                    @PathVariable long itemId,
                                    @RequestBody @Valid CreatingCommentDto commentDto) {
        return service.addComment(userId, itemId, commentDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(HEADER_USER_ID) long userId,
                       @PathVariable long itemId) {
        service.deleteItem(userId, itemId);
    }
}
