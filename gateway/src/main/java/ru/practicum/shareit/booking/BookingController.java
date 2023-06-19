package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> findBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                         @Min(0) @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @Min(1) @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findBookingsByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingsByOwnerId(@RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                        @RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                        @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get /bookings/owner?state={}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.findBookingsByOwnerId(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable long bookingId,
                                                 @RequestParam Boolean approved,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("PATCH /bookings/{}?approved={}, userId={}", bookingId, approved, userId);
        return bookingClient.approve(bookingId, userId, approved);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid AddBookingDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.createBooking(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }
}
