package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static final User user = new User(1L, "user", "user@yandex.ru");

    @Test
    void testEquals_whenUserIsEqual_thenTrue() {
        User newUser = new User(1L, "user", "user@yandex.ru");
        assertThat(user, equalTo(newUser));
    }

    @Test
    void testEquals_whenIdIsNotEqual_thenFalse() {
        User newUser = new User(2L, "user", "user@yandex.ru");
        assertNotEquals(user, newUser);
    }

    @Test
    void testEquals_whenIdIsNull_thenFalse() {
        User newUser = new User(null, "user", "user@yandex.ru");
        assertThat(user, not(equalTo(newUser)));
        assertThat(newUser, not(equalTo(user)));
    }

    @Test
    void testEquals_whenIsNotUser_thenFalse() {
        Object object = new Object();
        assertThat(user, not(equalTo(object)));
        assertThat(object, not(equalTo(user)));
    }

    @Test
    void testHashCode() {
        int hashCode = user.hashCode();
        int expectedHashCode = user.getClass().hashCode();
        assertEquals(expectedHashCode, hashCode);
    }

    @Test
    void testToString() {
        String expectedString = "User(id=1, name=user, email=user@yandex.ru)";
        String actualString = user.toString();
        assertEquals(expectedString, actualString);
    }
}