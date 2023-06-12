package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    GetBookingDto createBooking(AddBookingDto bookingDto, long userId);

    GetBookingDto getById(long bookingId, long userId);

    GetBookingDto approve(long bookingId, long userId, boolean approved);

    List<GetBookingDto> findBookingsByBookerId(long userId, BookingState state, int from, int size);

    List<GetBookingDto> findBookingsByOwnerId(long userId, BookingState state, int from, int size);

}
