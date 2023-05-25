package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

public interface BookingDao extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    Booking findFirstByItemIdAndStatusAndStartLessThanEqualOrderByStartDesc(long itemId, BookingStatus status, LocalDateTime startDate);

    Booking findFirstByItemIdAndStatusAndStartGreaterThanOrderByStart(long itemId, BookingStatus status, LocalDateTime startDate);

    Boolean existsByItemIdAndBookerIdAndStatusAndEndLessThan(long itemId, long userId, BookingStatus status, LocalDateTime end);
}
