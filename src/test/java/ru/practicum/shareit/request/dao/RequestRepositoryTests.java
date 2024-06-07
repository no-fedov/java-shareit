package ru.practicum.shareit.request.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.CollectionUtils;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = "/schema-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RequestRepositoryTests {
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Test get all request other users")
    public void givenUserAndRequestItem_whenGetAllOtherRequest_whenReturnZeroRequest() {
        //given
        User user = User.builder()
                .email("yandex@yandex.ru")
                .name("Zeliboba")
                .build();
        userRepository.save(user);

        Request request = Request.builder()
                .requester(user)
                .created(LocalDateTime.now())
                .description("Мне нужен слон")
                .build();

        requestRepository.save(request);

        //when
        List<Request> requests = requestRepository.findByRequesterIdNot(user.getId(), Pageable.unpaged());

        //then
        assertThat(CollectionUtils.isEmpty(requests)).isTrue();
    }

    @Test
    @DisplayName("Test get all requests by user id")
    public void givenUserAndThreeRequest_whenGetRequestByUserID_thenReturnThreeRequests() {
        //given
        User user = User.builder()
                .email("yandex@yandex.ru")
                .name("Zeliboba")
                .build();
        userRepository.save(user);

        Request request1 = Request.builder()
                .requester(user)
                .created(LocalDateTime.now())
                .description("Мне нужен слон")
                .build();

        Request request2 = Request.builder()
                .requester(user)
                .created(LocalDateTime.now())
                .description("Лопата")
                .build();

        Request request3 = Request.builder()
                .requester(user)
                .created(LocalDateTime.now())
                .description("Сон")
                .build();

        requestRepository.saveAll(List.of(request1,request2, request3));

        //when
        List<Request> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(user.getId());

        //then
        assertThat(CollectionUtils.isEmpty(requests)).isFalse();
        assertThat(requests.size()).isEqualTo(3);
    }
}
