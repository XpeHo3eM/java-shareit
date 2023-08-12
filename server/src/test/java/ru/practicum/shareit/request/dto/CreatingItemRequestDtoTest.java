package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreatingItemRequestDtoTest {
    @Autowired
    private JacksonTester<CreatingItemRequestDto> json;

    @Test
    void shouldSerialize() throws IOException {
        CreatingItemRequestDto dto = CreatingItemRequestDto.builder()
                .description("description")
                .build();

        JsonContent<CreatingItemRequestDto> result = json.write(dto);

        assertThat(result)
                .hasJsonPathStringValue("$.description")
                .extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

    @Test
    void shouldDeserialize() throws IOException {
        String content = "{\"description\":\"description\"}";

        ObjectContent<CreatingItemRequestDto> result = json.parse(content);

        assertThat(result)
                .hasFieldOrPropertyWithValue("description", "description");
    }
}
