package ru.practicum.shareit.util;

import ru.practicum.shareit.user.model.User;

public class UserUtil {
    public static User getUser1() {
        return User.builder()
                .id(1)
                .name("user1")
                .email("user1@mail.ru")
                .build();
    }

    public static User getUser2() {
        return User.builder()
                .id(2)
                .name("user2")
                .email("user2@mail.ru")
                .build();
    }

    public static User getUser3() {
        return User.builder()
                .id(3)
                .name("user3")
                .email("user3@mail.ru")
                .build();
    }
}
