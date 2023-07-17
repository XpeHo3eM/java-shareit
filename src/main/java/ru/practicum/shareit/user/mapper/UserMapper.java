package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.CreatingUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {
    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserDtoShort toDtoShort(User user) {
        return UserDtoShort.builder()
                .id(user.getId())
                .build();
    }

    public User toUser(CreatingUserDto creatingUserDto) {
        return User.builder()
                .name(creatingUserDto.getName())
                .email(creatingUserDto.getEmail())
                .build();
    }
}
