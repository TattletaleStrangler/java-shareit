package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestTest {

    private static final Long REQUEST_ID = 1L;
    private static final String DESCRIPTION = "description";
    private static final Long REQUESTER_ID = 1L;
    private static final User requester = User.builder()
            .id(REQUESTER_ID)
            .build();
    private static final LocalDateTime created = LocalDateTime.now();
    private static final ItemRequest itemRequest = ItemRequest.builder()
            .id(REQUEST_ID)
            .description(DESCRIPTION)
            .created(created)
            .requester(requester)
            .build();

    @Test
    void testEquals_whenTheSame_thenTrue() {
        assertThat(itemRequest, equalTo(itemRequest));
    }

    @Test
    void testEquals_whenEquals_thenTrue() {
        ItemRequest newRequest = ItemRequest.builder()
                .id(REQUEST_ID)
                .description(DESCRIPTION)
                .created(created)
                .requester(requester)
                .build();
        assertThat(newRequest, equalTo(itemRequest));
        assertThat(itemRequest, equalTo(newRequest));
    }

    @Test
    void testEquals_whenNull_thenFalse() {
        assertThat(itemRequest, not(equalTo(null)));
    }

    @Test
    void testEquals_whenNotItemRequest_thenFalse() {
        Object object = new Object();
        assertThat(itemRequest, not(equalTo(object)));
    }

    @Test
    void testEquals_whenIdIsNull_thenFalse() {
        ItemRequest newRequest = ItemRequest.builder()
                .id(null)
                .description(DESCRIPTION)
                .created(created)
                .requester(requester)
                .build();
        assertThat(newRequest, not(equalTo(itemRequest)));
        assertThat(itemRequest, not(equalTo(newRequest)));
    }

    @Test
    void testEquals_whenIdIsNotEquals_thenFalse() {
        ItemRequest newRequest = ItemRequest.builder()
                .id(REQUESTER_ID + 1L)
                .description(DESCRIPTION)
                .created(created)
                .requester(requester)
                .build();
        assertThat(newRequest, not(equalTo(itemRequest)));
        assertThat(itemRequest, not(equalTo(newRequest)));
    }

    @Test
    void testHashCode() {
        int expected = itemRequest.getClass().hashCode();
        assertEquals(expected, new ItemRequest().hashCode());
    }

    @Test
    void testToString() {
        String expected = "ItemRequest(" +
                "id=" + REQUEST_ID +
                ", description=" + DESCRIPTION +
                ", created=" + created +
                ')';
        assertEquals(expected, itemRequest.toString());
    }
}