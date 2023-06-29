package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.StatusType;

import java.time.LocalDate;

@Data
@Builder
public class Booking {
    private long id;
    private LocalDate start;
    private LocalDate end;
    private long itemId;
    private long bookerId;
    private StatusType status;
}
