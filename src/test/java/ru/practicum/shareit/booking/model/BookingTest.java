package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;

class BookingTest {
    private static final Long BOOKING_ID = 1L;
    private static final Long ITEM_ID = 1L;
    private static final Long BOOKER_ID = 1L;
    private static final Item item = Item.builder().id(ITEM_ID).build();
    private static final User booker = User.builder().id(BOOKER_ID).build();
    private static final LocalDateTime start = LocalDateTime.now().minusDays(1);
    private static final LocalDateTime end = LocalDateTime.now().plusDays(1);

    private static final Booking booking = Booking.builder()
            .id(BOOKING_ID)
            .status(BookingStatus.APPROVED)
            .start(start)
            .end(end)
            .item(item)
            .booker(booker)
            .build();

    @Test
    void testEquals_whenTheSame() {
        assertThat(booking, equalTo(booking));
    }

    @Test
    void testEquals_whenEqual() {
        Booking newBooking = Booking.builder()
                .id(BOOKING_ID)
                .status(BookingStatus.APPROVED)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .build();
        assertThat(newBooking, equalTo(booking));
        assertThat(booking, equalTo(newBooking));
    }

    @Test
    void testEquals_whenNull_thenFalse() {
        Booking newBooking = null;
        assertThat(newBooking, not(equalTo(booking)));
        assertThat(booking, not(equalTo(newBooking)));
    }

    @Test
    void testEquals_whenNotBooking_thenFalse() {
        Object object = new Object();
        assertThat(object, not(equalTo(booking)));
        assertThat(booking, not(equalTo(object)));
    }

    @Test
    void testEquals_whenIdIsNull_thenFalse() {
        Booking newBooking = Booking.builder()
                .id(null)
                .status(BookingStatus.APPROVED)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .build();
        assertThat(booking, not(equalTo(newBooking)));
        assertThat(newBooking, not(equalTo(booking)));
    }

    @Test
    void testEquals_whenIdIsNotEquals_thenFalse() {
        Booking newBooking = Booking.builder()
                .id(BOOKING_ID + 1)
                .status(BookingStatus.APPROVED)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .build();
        assertThat(booking, not(equalTo(newBooking)));
        assertThat(newBooking, not(equalTo(booking)));
    }

    @Test
    void testHashCode() {
        int expectedHashCode = booking.getClass().hashCode();
        assertThat(expectedHashCode,  equalTo(booking.hashCode()));
    }

    @Test
    void testToString() {
        String expectedToString = "Booking(" +
                "id=" + BOOKING_ID +
                ", start=" + start +
                ", end=" + end +
                ", status=" + BookingStatus.APPROVED +
                ")";
        assertThat(expectedToString,  equalTo(booking.toString()));
        assertThat(booking.toString(), equalTo(expectedToString));
    }
}