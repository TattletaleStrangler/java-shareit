package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSmall;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDtoSmall bookingDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос POST /bookings");
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable long bookingId, @RequestParam(defaultValue = "false") Boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос PATCH /bookings/{bookingId}?approved={approved}");
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /bookings/{bookingId}");
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findBookingsByUserId(@RequestParam(defaultValue = "ALL") BookingState state,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /bookings?state={state}");
        return bookingService.findBookingsByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingsByOwnerId(@RequestParam(defaultValue = "ALL") BookingState state,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /bookings/owner?state={state}");
        return bookingService.findBookingsByOwnerId(userId, state);
    }

}
