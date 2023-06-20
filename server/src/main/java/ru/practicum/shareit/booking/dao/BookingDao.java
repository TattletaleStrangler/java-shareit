package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingDao extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    Booking findFirstByItemIdAndStatusAndStartLessThanEqualOrderByStartDesc(long itemId, BookingStatus status, LocalDateTime startDate);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item IN :items " +
            "AND b.status = :status " +
            "AND b.start <= :startDate " +
            "ORDER BY b.start DESC"
    )
    List<Booking> findLastByItems(@Param("items") List<Item> items, @Param("status") BookingStatus status, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item IN :items " +
            "AND b.status = :status " +
            "AND b.start > :startDate " +
            "ORDER BY b.start"
    )
    List<Booking> findNextByItems(@Param("items") List<Item> items, @Param("status") BookingStatus status, @Param("startDate") LocalDateTime startDate);

    Booking findFirstByItemIdAndStatusAndStartGreaterThanOrderByStart(long itemId, BookingStatus status, LocalDateTime startDate);

    Boolean existsByItemIdAndBookerIdAndStatusAndEndLessThan(long itemId, long userId, BookingStatus status, LocalDateTime end);
}
