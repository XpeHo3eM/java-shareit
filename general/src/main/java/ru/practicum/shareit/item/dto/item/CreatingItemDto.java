package ru.practicum.shareit.item.dto.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.marker.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CreatingItemDto {
    @NotNull(groups = {OnCreate.class})
    @NotBlank(groups = {OnCreate.class})
    private String name;

    @NotNull(groups = {OnCreate.class})
    @NotBlank(groups = {OnCreate.class})
    private String description;

    @NotNull(groups = {OnCreate.class})
    private Boolean available;

    private Long requestId;
}
