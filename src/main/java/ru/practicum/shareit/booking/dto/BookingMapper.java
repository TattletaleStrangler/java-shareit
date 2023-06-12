package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class BookingMapper {

    public List<GetBookingDto> bookingListToDto(Iterable<Booking> bookings) {
        List<GetBookingDto> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(bookingToDto(booking));
        }

        return result;
    }

    public GetBookingDto bookingToDto(Booking booking) {
        return GetBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(new GetBookingDto.Booker(booking.getBooker().getId()))
                .item(new GetBookingDto.Item(booking.getItem().getId(), booking.getItem().getName()))
                .status(booking.getStatus())
                .build();
    }

    public Booking addBookingDtoToBooking(AddBookingDto addBookingDto, User booker, Item item, BookingStatus status) {
        return Booking.builder()
                .start(addBookingDto.getStart())
                .end(addBookingDto.getEnd())
                .booker(booker)
                .item(item)
                .status(status)
                .build();
    }

    public BookingDtoForItemDto bookingToBookingDtoForItemDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDtoForItemDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

}
