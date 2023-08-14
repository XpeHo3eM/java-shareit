package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CreatingUserDtoJsonTest {
    @Autowired
    private JacksonTester<CreatingUserDto> json;

    @Test
    void shouldSerialize() throws IOException {
        CreatingUserDto creatingUserDto = CreatingUserDto.builder()
                .name("user")
                .email("email@ya.ru")
                .build();

        JsonContent<CreatingUserDto> result = json.write(creatingUserDto);

        assertThat(result)
                .hasJsonPathStringValue("$.email")
                .extractingJsonPathStringValue("$.email").isEqualTo(creatingUserDto.getEmail());
        assertThat(result)
                .hasJsonPathStringValue("$.name")
                .extractingJsonPathStringValue("$.name").isEqualTo(creatingUserDto.getName());
    }

    @Test
    void shouldDeserialize() throws IOException {
        String user = "{\"name\":\"name\"," +
                "\"email\":\"email@ya.ru\"}";

        ObjectContent<CreatingUserDto> creatingUserDto = json.parse(user);

        assertThat(creatingUserDto)
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("email", "email@ya.ru");
    }
}
