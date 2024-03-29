package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dal.ItemService;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreatingCommentDto;
import ru.practicum.shareit.item.dto.item.CreatingItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;

import java.util.List;

import static ru.practicum.shareit.util.Constant.HEADER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto addItem(@RequestHeader(HEADER_USER_ID) long userId,
                           @RequestBody CreatingItemDto creatingItemDto) {
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
                                       @RequestParam Integer from,
                                       @RequestParam Integer size) {
        PageRequest page = PageRequest.of(from / size, size);

        return service.getAllItemsByUserId(userId, page);
    }

    @GetMapping("/search")
    public List<ItemDto> findItem(@RequestHeader(HEADER_USER_ID) long userId,
                                  @RequestParam String text,
                                  @RequestParam Integer from,
                                  @RequestParam Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by("id").ascending());

        return service.search(userId, text, page);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(HEADER_USER_ID) long userId,
                                    @PathVariable long itemId,
                                    @RequestBody CreatingCommentDto commentDto) {
        return service.addComment(userId, itemId, commentDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(HEADER_USER_ID) long userId,
                       @PathVariable long itemId) {
        service.deleteItem(userId, itemId);
    }
}
