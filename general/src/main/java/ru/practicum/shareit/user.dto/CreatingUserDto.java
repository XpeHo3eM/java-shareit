package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.marker.OnCreate;
import ru.practicum.shareit.util.marker.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CreatingUserDto {
    @NotNull(groups = {OnCreate.class})
    @NotBlank(groups = {OnCreate.class})
    private String name;

    @Email(groups = {OnCreate.class, OnUpdate.class})
    @NotNull(groups = {OnCreate.class})
    @NotEmpty(groups = {OnCreate.class})
    private String email;
}
