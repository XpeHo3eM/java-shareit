package ru.practicum.shareit.item.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.marker.OnCreate;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatingCommentDto {
    @NotBlank(groups = {OnCreate.class})
    private String text;
}
