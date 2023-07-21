package ru.practicum.shareit.item.dto.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreatingCommentDto {
    @NotBlank
    private String text;
}
