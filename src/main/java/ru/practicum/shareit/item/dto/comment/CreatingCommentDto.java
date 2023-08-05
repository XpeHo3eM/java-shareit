package ru.practicum.shareit.item.dto.comment;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatingCommentDto {
    @NotBlank
    private String text;
}
