package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public GetBookingDto createBooking(@RequestBody AddBookingDto bookingDto,
                                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос POST /bookings");
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public GetBookingDto approveBooking(@PathVariable long bookingId,
                                        @RequestParam Boolean approved,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос PATCH /bookings/{}?approved={}", bookingId, approved);
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public GetBookingDto getBooking(@PathVariable long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /bookings/{}", bookingId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<GetBookingDto> findBookingsByBookerId(@RequestParam BookingState state,
                                                      @RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(name = "from", defaultValue = "0") int from,
                                                      @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен запрос GET /bookings?state={}", state);
        return bookingService.findBookingsByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<GetBookingDto> findBookingsByOwnerId(@RequestParam BookingState state,
                                                     @RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен запрос GET /bookings/owner?state={}", state);
        return bookingService.findBookingsByOwnerId(userId, state, from, size);
    }

}
