package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.item.dto.item.CreatingItemDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CreatingItemDtoJsonTest {
    @Autowired
    private JacksonTester<CreatingItemDto> json;
    private final CreatingItemDto creatingItemDto = CreatingItemDto.builder()
            .name("itemName")
            .description("itemDescription")
            .available(true)
            .requestId(1L)
            .build();

    @Test
    void shouldSerialize() throws IOException {


        JsonContent<CreatingItemDto> result = json.write(creatingItemDto);

        assertThat(result)
                .hasJsonPathStringValue("$.name")
                .extractingJsonPathStringValue("$.name").isEqualTo(creatingItemDto.getName());
        assertThat(result)
                .hasJsonPathStringValue("$.description")
                .extractingJsonPathStringValue("$.description").isEqualTo(creatingItemDto.getDescription());
        assertThat(result)
                .hasJsonPathBooleanValue("$.available")
                .extractingJsonPathBooleanValue("$.available").isEqualTo(creatingItemDto.getAvailable());
        assertThat(result)
                .hasJsonPathNumberValue("$.requestId")
                .extractingJsonPathNumberValue("$.requestId").isEqualTo(creatingItemDto.getRequestId().intValue());

    }

    @Test
    void shouldDeserialize() throws IOException {
        String content = "{\"name\":\"itemName\"," +
                "\"description\":\"itemDescription\"," +
                "\"available\":\"true\"," +
                "\"requestId\":\"1\"}";

        ObjectContent<CreatingItemDto> result = json.parse(content);

        assertThat(result)
                .hasFieldOrPropertyWithValue("name", creatingItemDto.getName())
                .hasFieldOrPropertyWithValue("description", creatingItemDto.getDescription())
                .hasFieldOrPropertyWithValue("available", creatingItemDto.getAvailable())
                .hasFieldOrPropertyWithValue("requestId", creatingItemDto.getRequestId());
    }
}
