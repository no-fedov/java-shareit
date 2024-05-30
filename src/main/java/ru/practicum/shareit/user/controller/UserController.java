package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
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
    public UserDto addUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("UserController: Request for \"Add new user\" ({}, {})", userCreateDto.getName(), userCreateDto.getEmail());
        return userService.addUser(userCreateDto);
    }

    @GetMapping("/{id}")
    public UserDto findUser(@PathVariable int id) {
        log.info("UserController: Request for \"Find user by id\" id = {}", id);
        return userService.findUser(id);
    }

    @PatchMapping(("/{id}"))
    public UserDto updateUser(@PathVariable int id, @RequestBody UserUpdateDto userUpdateDto) {
        log.info("UserController: Request for \"Update user by id\" id = {}", id);
        return userService.updateUser(id, userUpdateDto);
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable int id) {
        log.info("UserController: Request for \"Delete user by id\" id = {}", id);
        return userService.deleteUser(id);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("UserController: Request for \"Get all users\"");
        return userService.getUsers();
    }
}