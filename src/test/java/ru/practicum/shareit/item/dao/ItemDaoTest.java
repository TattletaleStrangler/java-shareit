package ru.practicum.shareit.item.dao;

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
import static org.hamcrest.Matchers.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemDaoTest {
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

    private static final long REQUEST_ID1 = 1;
    private static final long REQUEST_ID2 = 2;

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
    private final TestEntityManager em;

    private final ItemDao itemDao;

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
    void findAllByOwnerIdOrderById() {
        int from = 0;
        int size = 10;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Item> items = itemDao.findAllByOwnerIdOrderById(owner2.getId(), page);

        assertThat(items, notNullValue());
        assertThat(items, hasSize(2));
        assertThat(items.get(0).getId(), equalTo(ITEM_ID2));
        assertThat(items.get(0).getName(), equalTo(item2.getName()));
        assertThat(items.get(0).getDescription(), equalTo(item2.getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(item2.getAvailable()));
        assertThat(items.get(0).getRequest(), notNullValue());
        assertThat(items.get(0).getRequest().getDescription(), equalTo(item2.getRequest().getDescription()));
        assertThat(items.get(0).getRequest().getRequester().getId(), equalTo(item2.getRequest().getRequester().getId()));
        assertThat(items.get(0).getRequest().getRequester().getName(), equalTo(item2.getRequest().getRequester().getName()));
        assertThat(items.get(0).getRequest().getRequester().getEmail(), equalTo(item2.getRequest().getRequester().getEmail()));
        assertThat(items.get(0).getOwner().getId(), equalTo(item2.getOwner().getId()));
        assertThat(items.get(0).getOwner().getName(), equalTo(item2.getOwner().getName()));
        assertThat(items.get(0).getOwner().getEmail(), equalTo(item2.getOwner().getEmail()));
        assertThat(items.get(1).getId(), equalTo(ITEM_ID3));
        assertThat(items.get(1).getName(), equalTo(item3.getName()));
        assertThat(items.get(1).getDescription(), equalTo(item3.getDescription()));
        assertThat(items.get(1).getAvailable(), equalTo(item3.getAvailable()));
        assertThat(items.get(1).getRequest(), nullValue());
        assertThat(items.get(1).getOwner().getId(), equalTo(item3.getOwner().getId()));
        assertThat(items.get(1).getOwner().getName(), equalTo(item3.getOwner().getName()));
        assertThat(items.get(1).getOwner().getEmail(), equalTo(item3.getOwner().getEmail()));
    }

    @Test
    void searchByText() {
        String text = "опИсанИе";
        int from = 0;
        int size = 10;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Item> items = itemDao.searchByText(text.toLowerCase(), page);

        assertThat(items, notNullValue());
        assertThat(items, hasSize(2));
        assertThat(items.get(0).getId(), equalTo(ITEM_ID1));
        assertThat(items.get(0).getName(), equalTo(item1.getName()));
        assertThat(items.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(items.get(0).getRequest(), notNullValue());
        assertThat(items.get(0).getRequest().getDescription(), equalTo(item1.getRequest().getDescription()));
        assertThat(items.get(0).getRequest().getRequester().getId(), equalTo(item1.getRequest().getRequester().getId()));
        assertThat(items.get(0).getRequest().getRequester().getName(), equalTo(item1.getRequest().getRequester().getName()));
        assertThat(items.get(0).getRequest().getRequester().getEmail(), equalTo(item1.getRequest().getRequester().getEmail()));
        assertThat(items.get(0).getOwner().getId(), equalTo(item1.getOwner().getId()));
        assertThat(items.get(0).getOwner().getName(), equalTo(item1.getOwner().getName()));
        assertThat(items.get(0).getOwner().getEmail(), equalTo(item1.getOwner().getEmail()));
        assertThat(items.get(1).getId(), equalTo(ITEM_ID2));
        assertThat(items.get(1).getName(), equalTo(item2.getName()));
        assertThat(items.get(1).getDescription(), equalTo(item2.getDescription()));
        assertThat(items.get(1).getAvailable(), equalTo(item2.getAvailable()));
        assertThat(items.get(1).getRequest(), notNullValue());
        assertThat(items.get(1).getRequest().getDescription(), equalTo(item2.getRequest().getDescription()));
        assertThat(items.get(1).getRequest().getRequester().getId(), equalTo(item2.getRequest().getRequester().getId()));
        assertThat(items.get(1).getRequest().getRequester().getName(), equalTo(item2.getRequest().getRequester().getName()));
        assertThat(items.get(1).getRequest().getRequester().getEmail(), equalTo(item2.getRequest().getRequester().getEmail()));
        assertThat(items.get(1).getRequest().getCreated(), equalTo(item2.getRequest().getCreated()));
        assertThat(items.get(1).getOwner().getId(), equalTo(item2.getOwner().getId()));
        assertThat(items.get(1).getOwner().getName(), equalTo(item2.getOwner().getName()));
        assertThat(items.get(1).getOwner().getEmail(), equalTo(item2.getOwner().getEmail()));
    }

    @Test
    void findAllByItemRequests() {
        itemRequest1.setId(REQUEST_ID1);
        itemRequest2.setId(REQUEST_ID2);

        List<Item> items = itemDao.findAllByItemRequests(List.of(itemRequest1, itemRequest2));

        assertThat(items, notNullValue());
        assertThat(items, hasSize(2));
        assertThat(items.get(0).getId(), equalTo(ITEM_ID1));
        assertThat(items.get(0).getName(), equalTo(item1.getName()));
        assertThat(items.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(items.get(0).getRequest(), notNullValue());
        assertThat(items.get(0).getRequest().getDescription(), equalTo(item1.getRequest().getDescription()));
        assertThat(items.get(0).getRequest().getRequester().getId(), equalTo(item1.getRequest().getRequester().getId()));
        assertThat(items.get(0).getRequest().getRequester().getName(), equalTo(item1.getRequest().getRequester().getName()));
        assertThat(items.get(0).getRequest().getRequester().getEmail(), equalTo(item1.getRequest().getRequester().getEmail()));
        assertThat(items.get(0).getOwner().getId(), equalTo(item1.getOwner().getId()));
        assertThat(items.get(0).getOwner().getName(), equalTo(item1.getOwner().getName()));
        assertThat(items.get(0).getOwner().getEmail(), equalTo(item1.getOwner().getEmail()));
        assertThat(items.get(1).getId(), equalTo(ITEM_ID2));
        assertThat(items.get(1).getName(), equalTo(item2.getName()));
        assertThat(items.get(1).getDescription(), equalTo(item2.getDescription()));
        assertThat(items.get(1).getAvailable(), equalTo(item2.getAvailable()));
        assertThat(items.get(1).getRequest(), notNullValue());
        assertThat(items.get(1).getRequest().getDescription(), equalTo(item2.getRequest().getDescription()));
        assertThat(items.get(1).getRequest().getRequester().getId(), equalTo(item2.getRequest().getRequester().getId()));
        assertThat(items.get(1).getRequest().getRequester().getName(), equalTo(item2.getRequest().getRequester().getName()));
        assertThat(items.get(1).getRequest().getRequester().getEmail(), equalTo(item2.getRequest().getRequester().getEmail()));
        assertThat(items.get(1).getRequest().getCreated(), equalTo(item2.getRequest().getCreated()));
        assertThat(items.get(1).getOwner().getId(), equalTo(item2.getOwner().getId()));
        assertThat(items.get(1).getOwner().getName(), equalTo(item2.getOwner().getName()));
        assertThat(items.get(1).getOwner().getEmail(), equalTo(item2.getOwner().getEmail()));
    }
}