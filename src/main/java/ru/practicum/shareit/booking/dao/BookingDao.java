package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

/**
 * TODO Sprint add-bookings.
 */
public interface BookingDao extends JpaRepository<Booking, Long> {

}
