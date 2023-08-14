package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.marker.OnCreate;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
public class CreatingBookingDto {
    @NotNull(groups = {OnCreate.class})
    @Positive(groups = {OnCreate.class})
    private Long itemId;

    @NotNull(groups = {OnCreate.class})
    @FutureOrPresent(groups = {OnCreate.class})
    private LocalDateTime start;

    @NotNull(groups = {OnCreate.class})
    @Future(groups = {OnCreate.class})
    private LocalDateTime end;
}
