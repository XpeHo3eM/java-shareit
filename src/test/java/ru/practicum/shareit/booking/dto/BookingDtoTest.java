package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.booking.model.StatusType;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.util.Constant.DATE_TIME_FORMATTER;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;
    private final LocalDateTime startTime = LocalDateTime.now().minusDays(10);
    private final LocalDateTime endTime = LocalDateTime.now().minusDays(2);


    @Test
    void shouldSerialize() throws IOException {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(startTime)
                .end(endTime)
                .status(StatusType.APPROVED)
                .build();

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathValue("$.start");
        assertThat(result).hasJsonPathValue("$.end");
        assertThat(result).hasJsonPathValue("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo(startTime.format(DATE_TIME_FORMATTER));
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo(endTime.format(DATE_TIME_FORMATTER));
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(StatusType.APPROVED.toString());
    }

    @Test
    void shouldDeserialize() throws IOException {
        String content = "{\"id\":\"1\"," +
                "\"start\":\"" + startTime + "\"," +
                "\"end\":\"" + endTime + "\"," +
                "\"status\":\"" + StatusType.APPROVED + "\"}";

        ObjectContent<BookingDto> result = json.parse(content);

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", startTime)
                .hasFieldOrPropertyWithValue("end", endTime)
                .hasFieldOrPropertyWithValue("status", StatusType.APPROVED);
    }
}
