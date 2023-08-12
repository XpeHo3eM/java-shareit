package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.item.dto.item.ItemDto;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJson {
    @Autowired
    private JacksonTester<ItemDto> json;
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(false)
            .requestId(1L)
            .comments(Collections.emptyList())
            .nextBooking(null)
            .lastBooking(null)
            .build();

    @Test
    void shouldSerialize() throws IOException {
        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result)
                .hasJsonPathNumberValue("$.id")
                .extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(result)
                .hasJsonPathStringValue("$.name")
                .extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result)
                .hasJsonPathStringValue("$.description")
                .extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result)
                .hasJsonPathBooleanValue("$.available")
                .extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result)
                .hasJsonPathNumberValue("$.requestId")
                .extractingJsonPathNumberValue("$.requestId").isEqualTo(itemDto.getRequestId().intValue());
    }

    @Test
    void shouldDeserialize() throws IOException {
        String content = "{\"id\":\"1\"," +
                "\"name\":\"name\"," +
                "\"description\":\"description\"," +
                "\"available\":\"false\"}";

        ObjectContent<ItemDto> result = json.parse(content);

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", itemDto.getId())
                .hasFieldOrPropertyWithValue("name", itemDto.getName())
                .hasFieldOrPropertyWithValue("description", itemDto.getDescription())
                .hasFieldOrPropertyWithValue("available", itemDto.getAvailable());
    }
}
