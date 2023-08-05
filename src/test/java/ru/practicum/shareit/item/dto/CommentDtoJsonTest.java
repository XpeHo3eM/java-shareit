package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoJsonTest {
    @Autowired
    private JacksonTester<CommentDto> json;
    private final LocalDateTime now = LocalDateTime.now();
    private final CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("comment")
            .created(now)
            .authorName("author")
            .build();

    @Test
    void shouldSerialize() throws IOException {
        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result)
                .hasJsonPathNumberValue("$.id")
                .extractingJsonPathNumberValue("$.id").isEqualTo(commentDto.getId().intValue());
        assertThat(result)
                .hasJsonPathStringValue("$.text")
                .extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
        assertThat(result)
                .hasJsonPathValue("$.created")
                .extractingJsonPathValue("$.created")
                .isEqualTo(commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result)
                .hasJsonPathStringValue("$.authorName")
                .extractingJsonPathStringValue("$.authorName").isEqualTo(commentDto.getAuthorName());
    }

    @Test
    void shouldDeserialize() throws IOException {
        String content = "{\"id\":\"1\"," +
                "\"text\":\"comment\"," +
                "\"created\":\"" + now + "\"," +
                "\"authorName\":\"author\"}";

        ObjectContent<CommentDto> result = json.parse(content);

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", commentDto.getId())
                .hasFieldOrPropertyWithValue("text", commentDto.getText())
                .hasFieldOrPropertyWithValue("created", commentDto.getCreated())
                .hasFieldOrPropertyWithValue("authorName", commentDto.getAuthorName());
    }
}
