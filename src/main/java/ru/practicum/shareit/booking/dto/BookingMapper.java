package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto bookingToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .status(booking.getStatus())
                .build();
    }

    public static Booking dtoSmallToBooking(BookingDtoSmall bookingDtoSmall, User booker, Item item, BookingStatus status) {
        return Booking.builder()
                .start(bookingDtoSmall.getStart())
                .end(bookingDtoSmall.getEnd())
                .booker(booker)
                .item(item)
                .status(status)
                .build();
    }

}
