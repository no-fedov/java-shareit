package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserCreateDto userCreateDto;
    private UserDto userDto;

    private UserUpdateDto userUpdateDto;

    @BeforeEach
    public void setup() {
        userCreateDto = UserCreateDto.builder()
                .email("john.doe@example.com")
                .name("John Doe")
                .build();
        userDto = UserDto.builder()
                .id(1)
                .email("john.doe@example.com")
                .name("John Doe")
                .build();

        userUpdateDto = UserUpdateDto.builder()
                .email("john.doe@example.com")
                .name("DOJA CAT")
                .build();
    }

    @Test
    public void testAddUser() throws Exception {
        Mockito.when(userService.addUser(any(UserCreateDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        Mockito.verify(userService, times(1)).addUser(any(UserCreateDto.class));
    }

    @Test
    public void testFindUser() throws Exception {
        Mockito.when(userService.findUser(anyInt())).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        Mockito.verify(userService, times(1)).findUser(1);
    }

    @Test
    public void testUpdateUser() throws Exception {
        Mockito.when(userService.updateUser(anyInt(), any(UserUpdateDto.class))).thenReturn(userDto);

        mockMvc.perform(patch("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        Mockito.verify(userService, times(1)).updateUser(anyInt(), any(UserUpdateDto.class));
    }

    @Test
    public void testDeleteUser() throws Exception {
        Mockito.when(userService.deleteUser(anyInt())).thenReturn(userDto);

        mockMvc.perform(delete("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        Mockito.verify(userService, times(1)).deleteUser(anyInt());
    }

    @Test
    public void testGetUsers() throws Exception {

        List<UserDto> userDtoList = List.of(userDto);
        Mockito.when(userService.getUsers()).thenReturn(userDtoList);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDtoList)));

        Mockito.verify(userService, times(1)).getUsers();
    }
}
