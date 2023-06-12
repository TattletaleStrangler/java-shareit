package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ItemTest {

    private static final Item item = new Item(1L, "name", "description", true, null, null);

    @Test
    void testEquals_whenItemEqual_thenTrue() {
        Item newItem = new Item(1L, "name", "description", true, null, null);
        assertThat(newItem, equalTo(item));
    }

    @Test
    void testEquals_whenTheSameItem() {
        assertThat(item, equalTo(item));
    }

    @Test
    void testEquals_whenIdNotEqual_thenFalse() {
        Item newItem = new Item(2L, "name", "description", true, null, null);
        assertNotEquals(newItem, equalTo(item));
        assertNotEquals(item, equalTo(newItem));
    }

    @Test
    void testEquals_whenIdNotIsNull_thenFalse() {
        Item newItem = new Item(null, "name", "description", true, null, null);
        assertNotEquals(newItem, equalTo(item));
        assertNotEquals(item, equalTo(newItem));
    }

    @Test
    void testEquals_whenNull_thenFalse() {
        assertNotEquals(null, equalTo(item));
        assertNotEquals(item, equalTo(null));
    }

    @Test
    void testEquals_whenNotItem_thenFalse() {
        Object newItem = new Object();
        assertNotEquals(newItem, equalTo(item));
        assertNotEquals(item, equalTo(newItem));
    }

    @Test
    void testEquals_whenNotItemAndNull_thenFalse() {
        Object newItem = null;
        assertNotEquals(newItem, equalTo(item));
        assertNotEquals(item, equalTo(newItem));
    }

    @Test
    void testEquals_whenIdIsNull_thenFalse() {
        Item newItem = new Item(null, "name", "description", true, null, null);
        assertNotEquals(newItem, equalTo(item));
        assertNotEquals(item, equalTo(newItem));
    }

    @Test
    void testHashCode() {
        int hashCode = item.hashCode();
        int expectedHashCode = item.getClass().hashCode();
        assertEquals(expectedHashCode, hashCode);
    }

    @Test
    void testToString() {
        String expectedString = "Item(" +
                "id=" + item.getId() +
                ", name=" + item.getName() +
                ", description=" + item.getDescription() +
                ", available=" + item.getAvailable() +
                ')';
        assertEquals(expectedString, item.toString());
    }
}