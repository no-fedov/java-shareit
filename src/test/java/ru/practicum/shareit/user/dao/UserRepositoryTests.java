package ru.practicum.shareit.user.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataAccessException;
import org.springframework.util.CollectionUtils;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.UserUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Test save user functionality")
    public void givenUserEntity_whenSave_thenUserIsCreated() {

        //given
        User user = User.builder()
                .email("yandex@yandex.ru")
                .name("Zeliboba")
                .build();

        //when
        User savedUser = userRepository.save(user);

        //then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test save user with duplicate email functionality")
    public void givenUserToSave_whenSave_thenEmailDuplicated() {

        //given
        User userToCreate = User.builder()
                .email("yandex@yandex.ru")
                .name("Zeliboba")
                .build();
        userRepository.save(userToCreate);

        User userWithDuplicateEmail = User.builder()
                .email("yandex@yandex.ru")
                .name("Zeliboba2")
                .build();

        //when

        //then
        assertThrows(DataAccessException.class, () -> userRepository.save(userWithDuplicateEmail));
    }

    @Test
    @DisplayName("Test update user functionality")
    public void givenUserEntityToUpdate_whenSave_thenEmailIsChanged() {

        //given
        String newEmail = "google@mail.com";

        User userToCreate = User.builder()
                .email("yandex@yandex.ru")
                .name("Zeliboba")
                .build();
        userRepository.save(userToCreate);

        //when
        User userToUpdate = userRepository.findById(userToCreate.getId())
                .orElse(null);
        userToUpdate.setEmail(newEmail);
        User updatedUser = userRepository.save(userToUpdate);

        //then
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
    }

    @Test
    @DisplayName("Test get user by id functionality")
    public void givenUserCreated_whenGetById_thenUserIsReturned() {

        //given
        User userToCreate = User.builder()
                .email("yandex@yandex.ru")
                .name("Zeliboba")
                .build();
        userRepository.save(userToCreate);

        //when
        User obtainUser = userRepository.findById(userToCreate.getId())
                .orElse(null);

        //then
        assertThat(obtainUser).isNotNull();
        assertThat(obtainUser).isEqualTo(userToCreate);
    }

    @Test
    @DisplayName("Test get all users functionality")
    public void givenThreeUsers_whenFindAll_thenReturnThreeUsers() {

        //given
        userRepository.saveAll(List.of(UserUtil.getUser1(), UserUtil.getUser2(), UserUtil.getUser3()));

        //when
        List<User> obtainedUsers = userRepository.findAll();

        //then
        assertThat(CollectionUtils.isEmpty(obtainedUsers)).isFalse();
        assertThat(obtainedUsers.size()).isEqualTo(3);
    }
}
