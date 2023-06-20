package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.data.domain.Sort.Direction.DESC;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentDaoTest {
    private static final long ITEM_ID1 = 1;
    private static final long ITEM_ID2 = 2;
    private static final long OWNER_ID1 = 1;
    private static final long OWNER_ID2 = 2;
    private static final long REQUESTER_ID1 = 3;
    private static final long REQUESTER_ID2 = 4;
    private static final long COMMENT_ID1 = 1;
    private static final long COMMENT_ID2 = 2;
    private static final long COMMENT_ID3 = 3;

    private User owner1;
    private User owner2;
    private User requester1;
    private User requester2;
    private Item item1;
    private Item item2;
    private Comment comment1;
    private Comment comment2;
    private Comment comment3;
    private final TestEntityManager em;

    private final CommentDao commentDao;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        UserDto ownerDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();
        owner1 = UserMapper.dtoToUser(ownerDto);
        em.persist(owner1);
        owner1.setId(OWNER_ID1);
        em.flush();

        UserDto ownerDto2 = UserDto.builder()
                .name("Serj Tankian 2")
                .email("serjtankian2@mail.com")
                .build();
        owner2 = UserMapper.dtoToUser(ownerDto2);
        em.persist(owner2);
        owner2.setId(OWNER_ID2);
        em.flush();

        UserDto requesterDto = UserDto.builder()
                .name("Daron Malakian")
                .email("daronmalakian@mail.com")
                .build();
        requester1 = UserMapper.dtoToUser(requesterDto);
        em.persist(requester1);
        requester1.setId(REQUESTER_ID1);
        em.flush();

        UserDto requesterDto2 = UserDto.builder()
                .name("Daron Malakian 2")
                .email("daronmalakian2@mail.com")
                .build();
        requester2 = UserMapper.dtoToUser(requesterDto2);
        em.persist(requester2);
        requester2.setId(REQUESTER_ID2);
        em.flush();

        ItemDto itemDto = ItemDto.builder()
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .build();
        item1 = ItemMapper.dtoToItem(itemDto, owner1, null);
        em.persist(item1);
        item1.setId(ITEM_ID1);
        em.flush();

        ItemDto itemDto2 = ItemDto.builder()
                .name("Вещь 2")
                .description("Описание вещи 2")
                .available(true)
                .build();
        item2 = ItemMapper.dtoToItem(itemDto2, owner2, null);
        em.persist(item2);
        item2.setId(ITEM_ID2);
        em.flush();

        LocalDateTime commentDate = LocalDateTime.parse("2023-06-04T18:00:00", dateTimeFormatter);
        String commentText = "Комментарий 1";
        CommentDto commentDto = new CommentDto(commentText);
        comment1 = ItemMapper.dtoToComment(commentDto, item1, requester1, commentDate);
        em.persist(comment1);
        comment1.setId(COMMENT_ID1);

        LocalDateTime commentDate2 = LocalDateTime.parse("2023-07-04T18:00:00", dateTimeFormatter);
        String commentText2 = "Комментарий 2";
        CommentDto commentDto2 = new CommentDto(commentText2);
        comment2 = ItemMapper.dtoToComment(commentDto2, item2, requester2, commentDate2);
        em.persist(comment2);
        comment2.setId(COMMENT_ID2);

        LocalDateTime commentDate3 = LocalDateTime.parse("2023-10-04T18:00:00", dateTimeFormatter);
        String commentText3 = "Комментарий 3";
        CommentDto commentDto3 = new CommentDto(commentText3);
        comment3 = ItemMapper.dtoToComment(commentDto3, item2, requester1, commentDate3);
        em.persist(comment3);
        comment3.setId(COMMENT_ID3);
    }

    @Test
    void findByItemId() {
        List<Comment> comments = commentDao.findByItemId(ITEM_ID2);

        assertThat(comments, notNullValue());
        assertThat(comments, hasSize(2));
        assertThat(comments.get(0).getId(), equalTo(comment2.getId()));
        assertThat(comments.get(0).getText(), equalTo(comment2.getText()));
        assertThat(comments.get(0).getCreated(), equalTo(comment2.getCreated()));
        assertThat(comments.get(0).getAuthor(), equalTo(requester2));
        assertThat(comments.get(0).getItem(), equalTo(item2));
        assertThat(comments.get(1).getId(), equalTo(comment3.getId()));
        assertThat(comments.get(1).getText(), equalTo(comment3.getText()));
        assertThat(comments.get(1).getCreated(), equalTo(comment3.getCreated()));
        assertThat(comments.get(1).getAuthor(), equalTo(requester1));
        assertThat(comments.get(1).getItem(), equalTo(item2));
    }

    @Test
    void findByItemIn() {
        List<Comment> comments = commentDao.findByItemIn(List.of(item1, item2), Sort.by(DESC, "created"));

        assertThat(comments, notNullValue());
        assertThat(comments, hasSize(3));
        assertThat(comments.get(0).getId(), equalTo(comment3.getId()));
        assertThat(comments.get(0).getText(), equalTo(comment3.getText()));
        assertThat(comments.get(0).getCreated(), equalTo(comment3.getCreated()));
        assertThat(comments.get(0).getAuthor(), equalTo(requester1));
        assertThat(comments.get(0).getItem(), equalTo(item2));
        assertThat(comments.get(1).getId(), equalTo(comment2.getId()));
        assertThat(comments.get(1).getText(), equalTo(comment2.getText()));
        assertThat(comments.get(1).getCreated(), equalTo(comment2.getCreated()));
        assertThat(comments.get(1).getAuthor(), equalTo(requester2));
        assertThat(comments.get(1).getItem(), equalTo(item2));
        assertThat(comments.get(2).getId(), equalTo(comment1.getId()));
        assertThat(comments.get(2).getText(), equalTo(comment1.getText()));
        assertThat(comments.get(2).getCreated(), equalTo(comment1.getCreated()));
        assertThat(comments.get(2).getAuthor(), equalTo(requester1));
        assertThat(comments.get(2).getItem(), equalTo(item1));
    }
}