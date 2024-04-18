package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    private UserMapper() {
    }

    public static void updateUserFromDto(User entity, UserDto dto) {
        if (dto == null) {
            return;
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            entity.setName(dto.getName());
        }

        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
    }
}
