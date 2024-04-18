package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("UserController: Request for \"Add new user\" ({}, {})", user.getName(), user.getEmail());
        return userService.addUser(user);
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable int id) {
        log.info("UserController: Request for \"Find user by id\" id = {}", id);
        return userService.findUser(id);
    }

    @PatchMapping(("/{id}"))
    public User updateUser(@PathVariable int id, @RequestBody UserDto user) {
        log.info("UserController: Request for \"Update user by id\" id = {}", id);
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public User deleteUser(@PathVariable int id) {
        log.info("UserController: Request for \"Delete user by id\" id = {}", id);
        return userService.deleteUser(id);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("UserController: Request for \"Get all users\"");
        return userService.getUsers();
    }
}
