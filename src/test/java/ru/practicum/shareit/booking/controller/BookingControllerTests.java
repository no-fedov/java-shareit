package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingOwnerService;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.ExceptionWrapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private BookingOwnerService bookingOwnerService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingCreateDto bookingCreateDto;
    private BookingDto bookingDto;

    @BeforeEach
    public void setup() {
        LocalDateTime start = LocalDateTime.now().plusSeconds(5);
        var end = start.plusDays(1);

        bookingCreateDto = BookingCreateDto.builder()
                .itemId(1)
                .booker(1)
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .build();
        bookingDto = BookingDto.builder()
                .id(1)
                .booker(UserDto.builder().build())
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .build();
    }

    @Test
    @DisplayName("Test add booking functionality with valid time")
    public void testAddBooking() throws Exception {
        Mockito.when(bookingService.addBooking(any(BookingCreateDto.class))).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));

        Mockito.verify(bookingService, Mockito.times(1)).addBooking(any(BookingCreateDto.class));
    }

    @Test
    @DisplayName("Test add booking functionality with not valid time")
    public void testAddBookingWithNoValidTime() throws Exception {
        LocalDateTime time = LocalDateTime.of(2000, 2, 2, 2, 2);

        BookingCreateDto bookingCreateDtoWithNoValidTime = BookingCreateDto.builder()
                .booker(1)
                .itemId(1)
                .start(time)
                .end(time)
                .status(Status.WAITING)
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDtoWithNoValidTime)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(Map.of("Request processing error", "Неверно указаны даты бронирования"))));
    }

    @Test
    public void testMakeBookingDecision() throws Exception {
        Mockito.when(bookingOwnerService.makeBookingDecision(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));

        Mockito.verify(bookingOwnerService, Mockito.times(1)).makeBookingDecision(anyInt(), anyInt(), anyBoolean());
    }

    @Test
    public void testFindBooking() throws Exception {
        Mockito.when(bookingService.findBooking(anyInt(), anyInt())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));

        Mockito.verify(bookingService, Mockito.times(1)).findBooking(anyInt(), anyInt());
    }

    @Test
    public void testFindBookingByCondition() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);
        Mockito.when(bookingService.findBookingByCondition(anyInt(), any(StateParam.class), any(Pageable.class))).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));

        Mockito.verify(bookingService, Mockito.times(1)).findBookingByCondition(anyInt(), any(StateParam.class), any(Pageable.class));
    }

    @Test
    public void testFindBookingOwnerByCondition() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);
        Mockito.when(bookingOwnerService.findBookingByCondition(anyInt(), any(StateParam.class), any(Pageable.class))).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));

        Mockito.verify(bookingOwnerService, Mockito.times(1)).findBookingByCondition(anyInt(), any(StateParam.class), any(Pageable.class));
    }

    @Test
    public void testFindBookingOwnerByConditionWithWrongState() throws Exception {

        String state = "USTAL";

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", state)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new ExceptionWrapper("Unknown state: "+ state))));
    }
}

