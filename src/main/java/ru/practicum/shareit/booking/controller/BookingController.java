package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.UnsupportState;
import ru.practicum.shareit.booking.service.BookingOwnerService;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
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
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @Min(1) @RequestParam(value = "from", required = false) Integer from,
                                                   @Min(1) @RequestParam(value = "size", required = false) Integer size) {
        Pageable page = getPage(from, size);
        try {
            StateParam stateParam = StateParam.valueOf(state);
            return bookingService.findBookingByCondition(userId, stateParam, page);
        } catch (IllegalArgumentException e) {
            throw new UnsupportState("Unknown state: " + state);
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingOwnerByCondition(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @Min(1) @RequestParam(value = "from", required = false) Integer from,
                                                        @Min(1) @RequestParam(value = "size", required = false) Integer size) {

        Pageable page = getPage(from, size);
        try {
            StateParam stateParam = StateParam.valueOf(state);
            return bookingOwnerService.findBookingByCondition(userId, stateParam, page);
        } catch (IllegalArgumentException e) {
            throw new UnsupportState("Unknown state: " + state);
        }
    }

    private Pageable getPage(Integer from, Integer size) {
        Pageable page = Pageable.unpaged();
        if (from != null && size != null) {
            page = PageRequest.of(from / size, size);
        }
        return page;
    }
}
