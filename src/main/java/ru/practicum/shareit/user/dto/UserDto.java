package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {
    private long id;

    @NotNull
    @NotEmpty
    private String name;

    @Email
    @NotNull
    @NotEmpty
    private String email;
}
