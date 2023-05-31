package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSmall;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingDtoSmall bookingDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос POST /bookings");
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable long bookingId, @RequestParam Boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос PATCH /bookings/{}?approved={}", bookingId, approved);
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /bookings/{}", bookingId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findBookingsByBookerId(@RequestParam(defaultValue = "ALL") BookingState state,
                                                   @RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                   @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        log.info("Получен запрос GET /bookings?state={}", state);
        return bookingService.findBookingsByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingsByOwnerId(@RequestParam(defaultValue = "ALL") BookingState state,
                                                  @RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        log.info("Получен запрос GET /bookings/owner?state={}", state);
        return bookingService.findBookingsByOwnerId(userId, state, from, size);
    }

}
