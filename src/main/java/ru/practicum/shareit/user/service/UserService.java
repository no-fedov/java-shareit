package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.model.NotFoundEntityException;
import ru.practicum.shareit.user.dao.UserRepository;
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

    private final UserRepository userRepository;

    public UserDto addUser(UserCreateDto userCreateDto) {
        log.info("UserService: addUser {}", userCreateDto);
        User user = UserMapper.mapToUserFromUserCreateDto(userCreateDto);
        return saveUser(user);
    }

    public UserDto findUser(int id) {
        log.info("UserService: Request has been received to search for a user with an id = {}", id);
        User user = getCurrentUserById(id);
        log.info("UserService: found user ({})", user);
        return UserMapper.mapToUserDtoFromUser(user);
    }

    public UserDto updateUser(int id, UserUpdateDto userUpdateDto) {
        log.info("UserService: update user with id = {}. Parameters for update: {}", id, userUpdateDto);
        User user = getCurrentUserById(id);
        User updatedUser = UserMapper.mapToUserFromUserUpdateDto(user, userUpdateDto);
        return saveUser(updatedUser);
    }

    public UserDto deleteUser(int id) {
        User deletedUser = getCurrentUserById(id);
        userRepository.deleteById(id);
        return UserMapper.mapToUserDtoFromUser(deletedUser);
    }

    public List<UserDto> getUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(UserMapper::mapToUserDtoFromUser)
                .collect(Collectors.toUnmodifiableList());
    }

    public User getCurrentUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException("User not found"));
        return user;
    }

    private UserDto saveUser(User user) {
        try {
            User newUser = userRepository.save(user);
            return UserMapper.mapToUserDtoFromUser(newUser);
        } catch (DataAccessException e) {
            throw new EmailIsBusy("It is not possible to update to this email it is busy");
        }
    }
}
