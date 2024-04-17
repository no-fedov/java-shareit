package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class User {
    private final int id;
    private String name;
    private String email;
}
