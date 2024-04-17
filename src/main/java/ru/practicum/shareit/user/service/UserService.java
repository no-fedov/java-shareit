package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundEntityException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validation.EmailIsBusy;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserService {
    private final UserDto userDto;

    public User addUser(User user) {
        emailIsBusy(user);
        return userDto.create(user);
    }

    public User findUser(int id) {
        User user = userDto.read(id).orElseThrow(() -> new NotFoundEntityException("User not found"));
        return user;
    }

    public User updateUser(User user) {
        emailIsBusy(user);
        userDto.read(user.getId());
        return userDto.update(user);
    }

    public User deleteUser(int id) {
        findUser(id);
        return userDto.delete(id);
    }

    public List<User> getUsers() {
        return userDto.getUsers();
    }

    private void emailIsBusy(User user) {
        if (userDto.emailIsBusy(user.getEmail())) {
            throw new EmailIsBusy("Email is busy");
        }
    }
}
