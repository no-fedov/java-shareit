package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(User user);

    Optional<User> read(int id);

    User update(User user);

    User delete(int id);

    List<User> getUsers();

    boolean emailIsBusy(String email);
}
