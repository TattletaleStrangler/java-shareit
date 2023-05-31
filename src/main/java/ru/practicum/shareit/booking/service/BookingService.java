package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSmall;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDtoSmall bookingDto, long userId);

    BookingDto getById(long bookingId, long userId);

    BookingDto approve(long bookingId, long userId, boolean approved);

    List<BookingDto> findBookingsByBookerId(long userId, BookingState state, int from, int size);

    List<BookingDto> findBookingsByOwnerId(long userId, BookingState state, int from, int size);

}
