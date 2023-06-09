package ru.practicum.shareit.item.model;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

class CommentTest {
    private static final LocalDateTime created = LocalDateTime.now();
    private static final User owner = User.builder().id(1L).name("owner name").email("owner@yandex.ru").build();
    private static final User requester = User.builder().id(2L).name("requester name").email("requester@yandex.ru").build();

    private static final Item item = Item.builder().id(1L).name("item name").description("description").available(true).owner(owner).build();

    private static final Comment comment = Comment.builder().id(1L).text("text").created(created).author(requester).item(item).build();

    @Test
    void testEquals_whenEqual() {
        Comment newComment = Comment.builder().id(1L).text("text").created(created).author(requester).item(item).build();
        assertThat(comment, equalTo(newComment));
    }

    @Test
    void testEquals_whenTheSame_thanEquals() {
        assertThat(comment, equalTo(comment));
    }

    @Test
    void testEquals_whenIsNull_thanNotEquals() {
        assertThat(comment, not(equalTo(null)));
        assertThat(null, not(equalTo(comment)));
    }

    @Test
    void testEquals_whenNotComment_thanNotEquals() {
        assertThat(new Object(), not(equalTo(comment)));
        assertThat(comment, not(equalTo(new Object())));
    }

    @Test
    void testEquals_whenIdIsDifferent_thanNotEquals() {
        long newId = 2L;
        Comment newComment = Comment.builder().id(newId).text("text").created(created).author(requester).item(item).build();
        assertThat(newComment, not(equalTo(comment)));
        assertThat(comment, not(equalTo(newComment)));
    }

    @Test
    void testEquals_whenItemIsDifferent_thanNotEquals() {
        Item newItem = Item.builder().id(2L).name("item name").description("description").available(true).owner(owner).build();
        Comment newComment = Comment.builder().id(comment.getId()).text(comment.getText()).created(created).author(requester).item(newItem).build();
        assertThat(newComment, not(equalTo(comment)));
        assertThat(comment, not(equalTo(newComment)));
    }

    @Test
    void testEquals_whenAuthorIsDifferent_thanNotEquals() {
        User newAuthor = User.builder().id(3L).build();
        Comment newComment = Comment.builder().id(comment.getId()).text(comment.getText()).created(created).author(newAuthor).item(item).build();
        assertThat(comment, not(equalTo(newComment)));
        assertThat(newComment, not(equalTo(comment)));
    }

    @Test
    void testEquals_whenCreatedIsDifferent_thanNotEquals() {
        LocalDateTime newCreated = created.plusDays(1);
        Comment newComment = Comment.builder().id(comment.getId()).text(comment.getText()).created(newCreated).author(requester).item(item).build();
        assertThat(comment, not(equalTo(newComment)));
        assertThat(newComment, not(equalTo(comment)));
    }

    @Test
    void testHashCode() {
        int hashCode = comment.hashCode();
        int expectedHashCode = comment.getId().hashCode();
        expectedHashCode = 31 * expectedHashCode + comment.getText().hashCode();
        expectedHashCode = 31 * expectedHashCode + item.hashCode();
        expectedHashCode = 31 * expectedHashCode + requester.hashCode();
        expectedHashCode = 31 * expectedHashCode + created.hashCode();
        assertThat(expectedHashCode, equalTo(hashCode));
    }

    @Test
    void testToString() {
        String expected = "Comment(" +
                "id=" + comment.getId() +
                ", text=" + comment.getText() +
                ", created=" + comment.getCreated() +
                ')';

        assertThat(expected, equalTo(comment.toString()));
    }
}