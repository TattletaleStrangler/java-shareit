package ru.practicum.shareit.booking.dao;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingDao {
    Booking createBooking(Booking booking);

    Optional<Booking> getById(Long id);

    Booking updateBooking(Booking booking);

    List<Booking> findAllBookings();

    void deleteBooking(Long id);
}
