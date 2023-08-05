package ru.practicum.shareit.booking.dto;

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
class CreatingBookingDtoTest {
    @Autowired
    private JacksonTester<CreatingBookingDto> json;
    private final LocalDateTime startTime = LocalDateTime.now().minusDays(10);
    private final LocalDateTime endTime = LocalDateTime.now().minusDays(2);
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void shouldSerialize() throws IOException {
        CreatingBookingDto dto = CreatingBookingDto.builder()
                .itemId(1L)
                .start(startTime)
                .end(endTime)
                .build();

        JsonContent<CreatingBookingDto> result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.itemId");
        assertThat(result).hasJsonPathValue("$.start");
        assertThat(result).hasJsonPathValue("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo(startTime.format(DATE_TIME_FORMATTER));
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo(endTime.format(DATE_TIME_FORMATTER));
    }

    @Test
    void shouldDeserialize() throws IOException {
        String content = "{\"itemId\":\"1\"," +
                "\"start\":\"" + startTime + "\"," +
                "\"end\":\"" + endTime + "\"}";

        ObjectContent<CreatingBookingDto> result = json.parse(content);

        assertThat(result)
                .hasFieldOrPropertyWithValue("itemId", 1L)
                .hasFieldOrPropertyWithValue("start", startTime)
                .hasFieldOrPropertyWithValue("end", endTime);
    }
}