package ru.practicum.shareit.request.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
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

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestDaoTest {

    private static final long ITEM_ID1 = 1;
    private static final long ITEM_ID2 = 2;
    private static final long ITEM_ID3 = 3;
    private static final long OWNER_ID1 = 1;
    private static final long OWNER_ID2 = 2;
    private static final long REQUESTER_ID1 = 3;
    private static final long REQUESTER_ID2 = 4;
    private static final long COMMENT_ID1 = 1;
    private static final long COMMENT_ID2 = 2;
    private static final long COMMENT_ID3 = 3;
    private static final long BOOKING_ID1 = 1;
    private static final long BOOKING_ID2 = 2;
    private static final long BOOKING_ID3 = 3;
    private static final long BOOKING_ID4 = 4;
    private static final long BOOKING_ID5 = 5;
    private static final long REQUEST_ID1 = 1;
    private static final long REQUEST_ID2 = 2;
    private static final long REQUEST_ID3 = 3;

    private User owner1;
    private User owner2;
    private User requester1;
    private User requester2;
    private Item item1;
    private Item item2;
    private Item item3;
    private Comment comment1;
    private Comment comment2;
    private Comment comment3;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private ItemRequest itemRequest3;
    private final TestEntityManager em;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final ItemRequestDao itemRequestDao;

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

        UserDto requesterDto1 = UserDto.builder()
                .name("Daron Malakian")
                .email("daronmalakian@mail.com")
                .build();
        requester1 = UserMapper.dtoToUser(requesterDto1);
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

        AddItemRequestDto addItemRequestDto1 = new AddItemRequestDto("Необходима вещь");
        itemRequest1 = ItemRequestMapper.dtoToItemRequest(addItemRequestDto1, requester1);
        em.persist(itemRequest1);
        itemRequest1.setId(REQUEST_ID1);

        ItemDto itemDto = ItemDto.builder()
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .build();
        item1 = ItemMapper.dtoToItem(itemDto, owner1, itemRequest1);
        em.persist(item1);
        item1.setId(ITEM_ID1);
        em.flush();

        AddItemRequestDto addItemRequestDto2 = new AddItemRequestDto("Необходим предмет 2");
        itemRequest2 = ItemRequestMapper.dtoToItemRequest(addItemRequestDto2, requester2);
        em.persist(itemRequest2);
        itemRequest2.setId(REQUEST_ID2);

        ItemDto itemDto2 = ItemDto.builder()
                .name("Предмет 2")
                .description("Описание предмета 2")
                .available(true)
                .build();
        item2 = ItemMapper.dtoToItem(itemDto2, owner2, itemRequest2);
        em.persist(item2);
        item2.setId(ITEM_ID2);
        em.flush();

        AddItemRequestDto addItemRequestDto3 = new AddItemRequestDto("Необходима приблуда");
        itemRequest3 = ItemRequestMapper.dtoToItemRequest(addItemRequestDto3, requester1);
        em.persist(itemRequest3);
        itemRequest3.setId(REQUEST_ID3);

        ItemDto itemDto3 = ItemDto.builder()
                .name("Вещь 3")
                .description("Подробности о вещи 3")
                .available(true)
                .build();
        item3 = ItemMapper.dtoToItem(itemDto3, owner2, null);
        em.persist(item3);
        item3.setId(ITEM_ID3);
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
    void findAllByRequesterIdOrderByCreatedDesc() {
        List<ItemRequest> requests = itemRequestDao.findAllByRequesterIdOrderByCreatedDesc(REQUESTER_ID1);

        assertThat(requests, notNullValue());
        assertThat(requests, hasSize(2));
        assertThat(requests.get(0).getId(), equalTo(itemRequest3.getId()));
        assertThat(requests.get(0).getDescription(), equalTo(itemRequest3.getDescription()));
        assertThat(requests.get(0).getCreated(), equalTo(itemRequest3.getCreated()));
        assertThat(requests.get(0).getRequester(), notNullValue());
        assertThat(requests.get(0).getRequester().getId(), equalTo(itemRequest3.getRequester().getId()));
        assertThat(requests.get(0).getRequester().getName(), equalTo(itemRequest3.getRequester().getName()));
        assertThat(requests.get(0).getRequester().getEmail(), equalTo(itemRequest3.getRequester().getEmail()));
        assertThat(requests.get(1).getId(), equalTo(itemRequest1.getId()));
        assertThat(requests.get(1).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(requests.get(1).getCreated(), equalTo(itemRequest1.getCreated()));
        assertThat(requests.get(1).getRequester(), notNullValue());
        assertThat(requests.get(1).getRequester().getId(), equalTo(itemRequest1.getRequester().getId()));
        assertThat(requests.get(1).getRequester().getName(), equalTo(itemRequest1.getRequester().getName()));
        assertThat(requests.get(1).getRequester().getEmail(), equalTo(itemRequest1.getRequester().getEmail()));
    }

    @Test
    void findAllByRequesterIdIsNotOrderByCreatedDesc() {
        int from = 0;
        int size = 5;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> requests = itemRequestDao.findAllByRequesterIdIsNotOrderByCreatedDesc(REQUESTER_ID1, page);

        assertThat(requests, notNullValue());
        assertThat(requests, hasSize(1));
        assertThat(requests.get(0).getId(), equalTo(itemRequest2.getId()));
        assertThat(requests.get(0).getDescription(), equalTo(itemRequest2.getDescription()));
        assertThat(requests.get(0).getCreated(), equalTo(itemRequest2.getCreated()));
        assertThat(requests.get(0).getRequester(), notNullValue());
        assertThat(requests.get(0).getRequester().getId(), equalTo(itemRequest2.getRequester().getId()));
        assertThat(requests.get(0).getRequester().getName(), equalTo(itemRequest2.getRequester().getName()));
        assertThat(requests.get(0).getRequester().getEmail(), equalTo(itemRequest2.getRequester().getEmail()));
    }
}