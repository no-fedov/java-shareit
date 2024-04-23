package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.model.NotFoundEntityException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.exception.validation.EmailIsBusy;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserDao userDao;

    public UserDto addUser(UserCreateDto userCreateDto) {
        log.info("UserService: addUser {}", userCreateDto);

        if (userDao.emailIsBusy(userCreateDto.getEmail())) {
            throw new EmailIsBusy("Email is busy");
        }

        User user = UserMapper.mapToUserFromUserCreateDto(userCreateDto);
        User newUser = userDao.create(user);
        return UserMapper.mapToUserDtoFromUser(newUser);
    }

    public UserDto findUser(int id) {
        log.info("UserService: Request has been received to search for a user with an id = {}", id);
        User user = getUserByID(id);
        log.info("UserService: found user ({})", user);
        return UserMapper.mapToUserDtoFromUser(user);
    }

    public UserDto updateUser(int id, UserUpdateDto userUpdateDto) {
        log.info("UserService: update user with id = {}. Parameters for update: {}", id, userUpdateDto);
        User user = getUserByID(id);

        emailIsBusy(user.getEmail(), userUpdateDto.getEmail());

        log.info("The user has updated the email = {}", userUpdateDto.getEmail());
        User updatedUser = UserMapper.mapToUserFromUserUpdateDto(user, userUpdateDto);
        User userInDB = userDao.update(updatedUser);

        return UserMapper.mapToUserDtoFromUser(userInDB);
    }

    public UserDto deleteUser(int id) {
        User deletedUser = userDao.delete(id);
        return UserMapper.mapToUserDtoFromUser(deletedUser);
    }

    public List<UserDto> getUsers() {
        List<User> userList = userDao.getUsers();
        return userList.stream()
                .map(UserMapper::mapToUserDtoFromUser)
                .collect(Collectors.toUnmodifiableList());
    }

    private User getUserByID(int id) {
        User user = userDao.read(id).orElseThrow(() -> new NotFoundEntityException("User not found"));
        return user;
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
