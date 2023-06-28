package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ItemRequest {
    private long id;
    private String description;
    private long requestorId;
    private LocalDate created;
}
