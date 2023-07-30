package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreatingItemRequestDto {
    @NotBlank
    private String description;
}
