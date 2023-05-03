package ru.practicum.shareit.booking.dao;

import ru.practicum.shareit.booking.model.Booking;

import java.util.*;

public class BookingDaoImplInMemory implements BookingDao{
    private Long id = 1L;
    private Map<Long, Booking> bookings = new HashMap<>();

    @Override
    public Booking createBooking(Booking booking) {
        booking.setId(getId());
        return bookings.put(booking.getId(), booking);
    }

    @Override
    public Optional<Booking> getById(Long id) {
        return Optional.ofNullable(bookings.get(id));
    }

    @Override
    public Booking updateBooking(Booking booking) {
        return bookings.put(booking.getId(), booking);
    }

    @Override
    public List<Booking> findAllBookings() {
        return new ArrayList<>(bookings.values());
    }

    @Override
    public void deleteBooking(Long id) {
        bookings.remove(id);
    }

    private long getId() {
        return id++;
    }
}
