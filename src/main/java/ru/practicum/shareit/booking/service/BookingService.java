package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BookingNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingService {

    private BookingDao bookingDao;

    public Booking createBooking(Booking booking) {
        return bookingDao.createBooking(booking);
    }

    public Booking getById(Long id) {
        return bookingDao.getById(id)
                .orElseThrow(() -> new BookingNotFoundException(""));
    }

    public Booking updateBooking(Booking booking) {
        return bookingDao.updateBooking(booking);
    }

    public List<Booking> findAllBookings() {
        return bookingDao.findAllBookings();
    }

    public void deleteBooking(Long id) {
        bookingDao.deleteBooking(id);
    }
}
