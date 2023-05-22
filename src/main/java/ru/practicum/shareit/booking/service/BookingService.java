package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSmall;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

@Service
public interface BookingService {

    BookingDto createBooking(BookingDtoSmall bookingDto, long userId);

    BookingDto getById(long bookingId, long userId);

    BookingDto approve(long bookingId, long userId, boolean approved);

    List<BookingDto> findBookingsByBookerId(long userId, BookingState state);

    List<BookingDto> findBookingsByOwnerId(long userId, BookingState state);

}
