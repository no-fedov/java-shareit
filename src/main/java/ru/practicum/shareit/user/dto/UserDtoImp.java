package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserDtoImp implements UserDto {
    private final Map<Integer, User> userRepository = new HashMap<>();
    private int generatorId = 0;

    @Override
    public User create(User user) {
        int id = generateID();
        User newUser = user.toBuilder().id(id).build();
        return userRepository.put(id, newUser);
    }

    @Override
    public Optional<User> read(int id) {
        return Optional.ofNullable(userRepository.get(id));
    }

    @Override
    public User update(User user) {
        return userRepository.put(user.getId(), user);
    }

    @Override
    public User delete(int id) {
        return userRepository.remove(id);
    }

    @Override
    public List<User> getUsers() {
        return Collections.unmodifiableList((List<User>) userRepository.values());
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
