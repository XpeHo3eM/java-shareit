package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;
    private static final LocalDateTime now = LocalDateTime.now();

    @Test
    void shouldSerialize() throws IOException {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("requestText")
                .created(now)
                .items(null)
                .build();

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result)
                .hasJsonPathNumberValue("$.id")
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result)
                .hasJsonPathStringValue("$.description")
                .extractingJsonPathStringValue("$.description").isEqualTo("requestText");
        assertThat(result)
                .hasJsonPathValue("$.created")
                .extractingJsonPathValue("$.created").isEqualTo(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result)
                .hasJsonPath("$.items")
                .extractingJsonPathValue("$.items").isEqualTo(null);
        assertThat(result)
                .hasJsonPath("$.description")
                .extractingJsonPathStringValue("$.description").isEqualTo("requestText");
        assertThat(result)
                .hasJsonPath("$.created")
                .extractingJsonPathValue("$.created").isEqualTo(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    }

    @Test
    void shouldDeserialize() throws IOException {
        String content = "{\"id\":\"1\"," +
                "\"description\":\"description\"," +
                "\"created\":\"" + now + "\"}";

        ObjectContent<ItemRequestDto> result = json.parse(content);

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", "description")
                .hasFieldOrPropertyWithValue("created", now)
                .hasFieldOrPropertyWithValue("items", null);
    }
}
