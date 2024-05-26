package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.UnsupportState;
import ru.practicum.shareit.booking.service.BookingOwnerService;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private final BookingOwnerService bookingOwnerService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Integer booker,
                                 @RequestBody @Valid BookingCreateDto bookingCreateDto) {
        bookingCreateDto.valid();
        bookingCreateDto.setBooker(booker);
        return bookingService.addBooking(bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto makeBookingDecision(@RequestHeader("X-Sharer-User-Id") Integer ownerItemId,
                                          @PathVariable int bookingId, @RequestParam boolean approved) {
        return bookingOwnerService.makeBookingDecision(ownerItemId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable int bookingId) {
        return bookingService.findBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findBookingByCondition(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        try {
            StateParam stateParam = StateParam.valueOf(state);
            return bookingService.findBookingByCondition(userId, stateParam);
        } catch (IllegalArgumentException e) {
            throw new UnsupportState("Unknown state: " + state);
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingOwnerByCondition(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                        @RequestParam(defaultValue = "ALL") String state) {

        try {
            StateParam stateParam = StateParam.valueOf(state);
            return bookingOwnerService.findBookingByCondition(userId, stateParam);
        } catch (IllegalArgumentException e) {
            throw new UnsupportState("Unknown state: " + state);
        }
    }
}
