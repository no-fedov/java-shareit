package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
public class RequestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private RequestCreateDto requestCreateDto;
    private RequestDto requestDto;
    private RequestDtoWithItems requestDtoWithItems;

    @BeforeEach
    public void setup() {
        requestCreateDto = RequestCreateDto.builder()
                .description("Test description")
                .requester(1)
                .created(LocalDateTime.now())
                .build();

        requestDto = RequestDto.builder()
                .id(1)
                .itemDto(ItemDto.builder().build())
                .description("Test description")
                .requester(1)
                .created(LocalDateTime.now())
                .build();

        requestDtoWithItems = RequestDtoWithItems.builder()
                .id(1)
                .description("Test description")
                .build();

    }

    @Test
    public void testAddRequest() throws Exception {
        Mockito.when(requestService.addRequest(any(RequestCreateDto.class))).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));

        Mockito.verify(requestService, Mockito.times(1)).addRequest(any(RequestCreateDto.class));
    }

    @Test
    public void testGetRequests() throws Exception {
        List<RequestDtoWithItems> requests = List.of(requestDtoWithItems);
        Mockito.when(requestService.getRequests(anyInt())).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));

        Mockito.verify(requestService, Mockito.times(1)).getRequests(anyInt());
    }

    @Test
    public void testGetRequest() throws Exception {
        Mockito.when(userService.getCurrentUserById(anyInt())).thenReturn(null);
        Mockito.when(requestService.getRequestById(anyInt())).thenReturn(requestDtoWithItems);

        mockMvc.perform(get("/requests/{id}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(requestDtoWithItems)));

        Mockito.verify(userService, Mockito.times(1)).getCurrentUserById(anyInt());
        Mockito.verify(requestService, Mockito.times(1)).getRequestById(anyInt());
    }

    @Test
    public void testGetPageRequest() throws Exception {
        List<RequestDtoWithItems> requests = List.of(requestDtoWithItems);
        Mockito.when(requestService.getPageRequest(anyInt(), any(Pageable.class))).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));

        Mockito.verify(requestService, Mockito.times(1)).getPageRequest(anyInt(), any(Pageable.class));
    }
}
