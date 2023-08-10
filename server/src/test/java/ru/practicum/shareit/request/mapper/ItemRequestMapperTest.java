package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class ItemRequestMapperTest {
    @Autowired
    ItemRequestMapper mapper;

    @Test
    void shouldGetNullWithNullParam() {
        assertNull(mapper.toItemRequestDto(null));
        assertNull(mapper.toItemRequest(null));
        assertNull(mapper.toItemRequestReplyDto(null));
    }

    @Test
    void shouldGetItemRequestReplyDtoFromItem() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .itemRequest(itemRequest)
                .build();

        assertThat(mapper.toItemRequestReplyDto(item))
                .hasFieldOrPropertyWithValue("id", item.getId())
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("available", item.getAvailable())
                .hasFieldOrPropertyWithValue("requestId", item.getItemRequest().getId());
    }
}
