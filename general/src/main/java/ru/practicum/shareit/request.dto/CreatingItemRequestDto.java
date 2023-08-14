package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.marker.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatingItemRequestDto {
    @NotBlank(groups = {OnCreate.class})
    @NotNull(groups = {OnCreate.class})
    private String description;
}
