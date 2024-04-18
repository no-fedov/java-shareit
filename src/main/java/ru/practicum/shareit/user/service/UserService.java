package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.model.NotFoundEntityException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validation.EmailIsBusy;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserDao userDao;

    public User addUser(User user) {
        log.info("UserService: addUser {}", user);

        if (userDao.emailIsBusy(user.getEmail())) {
            throw new EmailIsBusy("Email is busy");
        }

        return userDao.create(user);
    }

    public User findUser(int id) {
        log.info("UserService: Request has been received to search for a user with an id = {}", id);
        User user = userDao.read(id).orElseThrow(() -> new NotFoundEntityException("User not found"));
        log.info("UserService: found user ({})", user);
        return user;
    }

    public User updateUser(int id, UserDto userDto) {
        log.info("UserService: update user with id = {}. Parameters for update: {}", id, userDto);
        User findUser = findUser(id);

        emailIsBusy(findUser.getEmail(), userDto.getEmail());

        log.info("The user has updated the email = {}", userDto.getEmail());
        UserMapper.updateUserFromDto(findUser, userDto);

        return userDao.update(findUser);
    }

    public User deleteUser(int id) {
        findUser(id);
        return userDao.delete(id);
    }

    public List<User> getUsers() {
        return userDao.getUsers();
    }

    private void emailIsBusy(String emailUser, String emailUserDto) {
        if (emailUserDto == null) {
            return;
        }
        log.info("UserService: check email is busy");
        if (!emailUser.equals(emailUserDto) && userDao.emailIsBusy(emailUserDto)) {
            log.info("UserService: email = {} is busy", emailUserDto);
            throw new EmailIsBusy("It is not possible to update to this email it is busy");
        }
        log.info("UserService: email is not busy");
    }
}
