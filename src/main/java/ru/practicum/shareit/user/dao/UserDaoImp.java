package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class UserDaoImp implements UserDao {
    private final Map<Integer, User> userRepository = new HashMap<>();
    private int generatorId = 0;

    @Override
    public User create(User user) {
        int id = generateID();
        User newUser = user.toBuilder().id(id).build();
        userRepository.put(id, newUser);
        log.info("UserDtoImp: add new user ({})", newUser);
        return newUser;
    }

    @Override
    public Optional<User> read(int id) {
        log.info("UserDtoImp: find user with id = {}", id);
        return Optional.ofNullable(userRepository.get(id));
    }

    @Override
    public User update(User user) {
        log.info("UserDtoImp: user with id = {} updated ({})", user.getId(), user);
        userRepository.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(int id) {
        log.info("UserDtoImp: delete user with id = {}", id);
        return userRepository.remove(id);
    }

    @Override
    public List<User> getUsers() {
        log.info("UserDtoImp: find all users");
        return userRepository.values().stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean emailIsBusy(String email) {
        return userRepository.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    private int generateID() {
        return ++generatorId;
    }
}
