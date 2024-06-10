package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.service.ItemOwnerService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestServiceImplTests {
    @InjectMocks
    private RequestServiceImpl requestService;

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserService userService;

    @Mock
    private ItemOwnerService itemOwnerService;

    @Mock
    private ItemService itemService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1)
                .name("Vasya")
                .email("pupkin@mail.ru")
                .build();
    }

    @Test
    @DisplayName("Test add request funtionality")
    public void givenRequestCreateDto_whenAddRequest_whenReturnRequestDto() {

        //given
        RequestCreateDto requestCreateDto = RequestCreateDto.builder()
                .created(LocalDateTime.of(2023, 10, 1, 1, 1))
                .requester(1)
                .description("GUSSI slanci")
                .build();

        Request request = Request.builder()
                .id(1)
                .requester(user)
                .created(LocalDateTime.of(2023, 10, 1, 1, 1))
                .description("GUSSI slanci")
                .build();

        Mockito.when(userService.getCurrentUserById(anyInt())).thenReturn(user);
        Mockito.when(requestRepository.save(any(Request.class))).thenReturn(request);

        //when
        var requestDto = requestService.addRequest(requestCreateDto);

        //then
        assertThat(requestDto).isNotNull();
    }

    @Test
    @DisplayName("Test get request by user id functionality")
    public void givenUserAndOneRequest_whenGetRequestByUSerId_thenReturnOneRequest() {
        Request request = Request.builder()
                .id(1)
                .created(LocalDateTime.of(2023, 10, 1, 1, 1))
                .description("GUSSI slanci")
                .build();

        Mockito.when(userService.getCurrentUserById(anyInt())).thenReturn(user);
        Mockito.when(requestRepository.findByRequesterIdOrderByCreatedDesc(anyInt())).thenReturn(List.of(request));
        Mockito.when(itemOwnerService.getItemsByRequestIds(any(Collection.class))).thenReturn(List.of());

        var listRequestDtoWithItems = requestService.getRequests(user.getId());
        assertThat(listRequestDtoWithItems).isNotNull();
    }

    @Test
    @DisplayName("Test get request by id")
    public void givenId_whenGetRequestById_thenReturnRequest() {

        //given
        int id = 1;
        Mockito.when(requestRepository.findById(id)).thenReturn(Optional.ofNullable(Request.builder()
                .id(1)
                .created(LocalDateTime.of(2023, 10, 1, 1, 1))
                .description("GUSSI slanci")
                .build()));

        Mockito.when(itemService.getItemsByRequestId(anyInt())).thenReturn(null);

        //when
       var requestDto = requestService.getRequestById(id);

        //then
        assertThat(requestDto).isNotNull();
    }


}
