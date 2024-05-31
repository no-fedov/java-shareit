package ru.practicum.shareit.user.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.SQLWarningException;
import ru.practicum.shareit.exception.model.NotFoundEntityException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.exception.validation.EmailIsBusy;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.UserUtil;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Test save user functionality")
    public void givenUserCreateDto_whenSaveUser_thenRepositoryIsCalled() {
        //given
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("Кузьма")
                .email("mail@mail.ru")
                .build();

        User savedUser = User.builder()
                .id(1)
                .name("Кузьма")
                .email("mail@mail.ru")
                .build();

        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);


        //when
        UserDto userDto = userService.addUser(userCreateDto);

        //then
        assertThat(userDto).isNotNull();
        assertThat(userDto.getEmail()).isEqualTo(savedUser.getEmail());
        assertThat(userDto.getName()).isEqualTo(savedUser.getName());
        assertThat(userDto.getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("Test save user with duplicate email functionality")
    public void givenUserCreateDtoWithDublicatedEmail_whenSaveUser_thenThrowException() {

        //given
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("Кузьма")
                .email("mail@mail.ru")
                .build();


        Mockito.when(userRepository.save(any(User.class)))
                .thenThrow(new SQLWarningException(null, null));

        //when

        //then
        assertThrows(EmailIsBusy.class, () -> userService.addUser(userCreateDto));
    }

    @Test
    @DisplayName("Test get user by id functionality")
    public void givenUserDtoWithId_whenFindUser_thenReturnUser() {

        //given
        int userId = 1;
        UserDto userDto = UserDto.builder()
                .id(userId)
                .name("кузя")
                .email("лакомкин@mail.ru")
                .build();

        User savedUser = User.builder()
                .id(userId)
                .name("кузя")
                .email("лакомкин@mail.ru")
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(savedUser));
        //when
        UserDto findUserDto = userService.findUser(userId);
        //then
        assertThat(findUserDto).isEqualTo(userDto);
    }

    @Test
    @DisplayName("Test get user by id will throw exception")
    public void givenId_whenFindUser_thenThrowException() {

        //given
        int userId = 1;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        //when

        //then
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> userService.findUser(userId));
        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

    @Test
    @DisplayName("Test update user functionality")
    public void givenIdAndUserUpdateDto_whenUpdateUser_thenReturnUpdatedUser() {
        //given
        int userId = 1;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("ZZZVVV")
                .build();

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("DOOM")
                .email("DOOM@DOOM.ru")
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .name("DOOM")
                .email("DOOM@DOOM.ru")
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        //when
        UserDto updatedUserDto = userService.updateUser(userId, userUpdateDto);

        //then
        assertThat(updatedUserDto).isNotNull();
        assertThat(updatedUserDto.getEmail()).isEqualTo(userUpdateDto.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    @DisplayName("Test update user with duplicate email functionality")
    public void givenIdAndUserUpdateDto_whenUpdateUser_thenExceptionIsThrow() {
        //given
        int userId = 1;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("ZZZVVV")
                .build();

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("DOOM")
                .email("DOOM@DOOM.ru")
                .build();


        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(any(User.class)))
                .thenThrow(new SQLWarningException(null, null));

        //when
        EmailIsBusy exception = assertThrows(EmailIsBusy.class, ()-> userService.updateUser(userId,userUpdateDto));

        //then
        assertThat(exception.getMessage()).isEqualTo("It is not possible to update to this email it is busy");
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    @DisplayName("Test delete user by id functionality")
    public void givenId_whenDeleteBuId_thenReturnDeletedUser() {
        //given
        int userId = 1;
        User user = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("ZZZVVV")
                .build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //when
        UserDto deletedUser = userService.deleteUser(userId);

        //then
        verify(userRepository, times(1)).deleteById(userId);
        assertThat(deletedUser.getId()).isEqualTo(user.getId());
        assertThat(deletedUser.getName()).isEqualTo(user.getName());
        assertThat(deletedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Test get all users functionality")
    public void givebThreeUsers_whenGetAllUsers_whenReturnThreeUsers() {
        //given
        List<User> users = List.of(UserUtil.getUser1(), UserUtil.getUser2(), UserUtil.getUser3());
        Mockito.when(userRepository.findAll()).thenReturn(users);
        //when
        List<UserDto> userDtos = userService.getUsers();
        //then
        assertThat(userDtos.size()).isEqualTo(users.size());
    }
}
