package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.item.dto.comment.CreatingCommentDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CreatingCommentDtoJsonTest {
    @Autowired
    private JacksonTester<CreatingCommentDto> json;
    private static final CreatingCommentDto creatingCommentDto = CreatingCommentDto.builder()
            .text("comment")
            .build();

    @Test
    void shouldSerialize() throws IOException {
        JsonContent<CreatingCommentDto> result = json.write(creatingCommentDto);

        assertThat(result)
                .hasJsonPathStringValue("$.text")
                .extractingJsonPathStringValue("$.text").isEqualTo(creatingCommentDto.getText());
    }

    @Test
    void shouldDeserialize() throws IOException {
        String content = "{\"text\":\"comment\"}";

        ObjectContent<CreatingCommentDto> result = json.parse(content);

        assertThat(result)
                .hasFieldOrPropertyWithValue("text", creatingCommentDto.getText());
    }
}
