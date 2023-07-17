package ru.practicum.shareit.item.dto.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDtoShort {
    private long id;
    private String name;
}
