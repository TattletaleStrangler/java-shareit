package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ItemServiceImplTest {

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final EntityManager em;
    private final ItemService itemService;

    @Test
    void createItem() {
        long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .build();

        UserDto userDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();

        User user = UserMapper.dtoToUser(userDto);
        em.persist(user);
        user.setId(1L);
        em.flush();

        itemService.createItem(itemDto, user.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemId)
                .getSingleResult();

        assertThat(item, notNullValue());
        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void getById() {
        long itemId = 1L;
        long ownerId = 1L;
        long requesterId = 2L;
        long requestId = 1L;
        long commentId = 1L;
        long bookingId = 1L;

        UserDto ownerDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();

        UserDto requesterDto = UserDto.builder()
                .name("Daron Malakian")
                .email("daronmalakian@mail.com")
                .build();

        User owner = UserMapper.dtoToUser(ownerDto);
        em.persist(owner);
        owner.setId(ownerId);
        em.flush();
        User requester = UserMapper.dtoToUser(requesterDto);
        em.persist(requester);
        requester.setId(requesterId);
        em.flush();

        AddItemRequestDto addItemRequestDto1 = new AddItemRequestDto("Описание запроса 1");
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(addItemRequestDto1, requester);
        em.persist(itemRequest);
        itemRequest.setId(requestId);

        ItemDto itemDto = ItemDto.builder()
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .requestId(requestId)
                .build();

        Item item = ItemMapper.dtoToItem(itemDto, owner, itemRequest);
        em.persist(item);
        item.setId(itemId);
        em.flush();

        LocalDateTime nextBookingStartDate = LocalDateTime.parse("2023-07-23T17:40:50", dateTimeFormatter);
        LocalDateTime nextBookingEndDate = LocalDateTime.parse("2023-07-24T17:40:50", dateTimeFormatter);
        AddBookingDto addBookingDto = new AddBookingDto(nextBookingStartDate, nextBookingEndDate, itemId);
        Booking booking = BookingMapper.addBookingDtoToBooking(addBookingDto, requester, item, BookingStatus.APPROVED);
        em.persist(booking);
        booking.setId(bookingId);

        LocalDateTime firstCommentDate = LocalDateTime.parse("2023-06-04T18:00:00", dateTimeFormatter);
        String commentText = "Комментарий 1";
        CommentDto commentDto = new CommentDto(commentText);
        Comment comment = ItemMapper.dtoToComment(commentDto, item, requester, firstCommentDate);
        em.persist(comment);
        comment.setId(commentId);

        ItemDtoWithBooking savedItem = itemService.getById(itemId, owner.getId());

        assertThat(savedItem, notNullValue());
        assertThat(savedItem.getId(), equalTo(itemId));
        assertThat(savedItem.getName(), equalTo(itemDto.getName()));
        assertThat(savedItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(savedItem.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(savedItem.getRequestId(), equalTo(itemDto.getRequestId()));
        assertThat(savedItem.getNextBooking(), notNullValue());
        assertThat(savedItem.getNextBooking().getId(), equalTo(bookingId));
        assertThat(savedItem.getNextBooking().getItemId(), equalTo(itemId));
        assertThat(savedItem.getNextBooking().getStart(), equalTo(nextBookingStartDate));
        assertThat(savedItem.getNextBooking().getEnd(), equalTo(nextBookingEndDate));
        assertThat(savedItem.getNextBooking().getBookerId(), equalTo(booking.getBooker().getId()));
        assertThat(savedItem.getComments(), notNullValue());
        assertThat(savedItem.getComments(), hasSize(1));
        assertThat(savedItem.getComments().get(0).getText(), equalTo(commentText));
        assertThat(savedItem.getComments().get(0).getAuthorName(), equalTo(requester.getName()));
        assertThat(savedItem.getComments().get(0).getCreated(), equalTo(firstCommentDate));
    }

    @Test
    void updateItem() {
        long itemId = 1L;
        ItemDto itemDtoOld = ItemDto.builder()
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .build();

        UserDto userDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();

        User user = UserMapper.dtoToUser(userDto);
        em.persist(user);
        user.setId(1L);
        em.flush();

        em.persist(ItemMapper.dtoToItem(itemDtoOld, user, null));
        em.flush();

        ItemDto itemDtoNew = ItemDto.builder()
                .name("Новая Вещь")
                .build();

        itemService.updateItem(itemDtoNew, itemId, user.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemId)
                .getSingleResult();

        assertThat(item, notNullValue());
        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getName(), equalTo(itemDtoNew.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoOld.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDtoOld.getAvailable()));
    }

    @Test
    void findAllItemsByOwnerId() {
        long ownerId = 1L;
        long ownerId2 = 2L;
        long requesterId = 3L;
        long itemId = 1L;
        long itemId2 = 2L;
        long itemId3 = 3L;
        long requestId = 1L;
        long bookingId = 1L;
        long bookingId2 = 2L;
        long commentId = 1L;

        UserDto ownerDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();

        UserDto ownerDto2 = UserDto.builder()
                .name("John Dolmayan")
                .email("johndolmayan@mail.com")
                .build();

        UserDto requesterDto = UserDto.builder()
                .name("Daron Malakian")
                .email("daronmalakian@mail.com")
                .build();

        User owner = UserMapper.dtoToUser(ownerDto);
        em.persist(owner);
        owner.setId(ownerId);
        User owner2 = UserMapper.dtoToUser(ownerDto2);
        em.persist(owner2);
        owner2.setId(ownerId2);
        em.flush();
        User requester = UserMapper.dtoToUser(requesterDto);
        em.persist(requester);
        requester.setId(requesterId);
        em.flush();

        ItemDto itemDto1 = ItemDto.builder()
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .build();
        Item item = ItemMapper.dtoToItem(itemDto1, owner, null);
        em.persist(item);
        item.setId(itemId);
        em.flush();

        AddItemRequestDto addItemRequestDto1 = new AddItemRequestDto("Необходим предмет 2");
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(addItemRequestDto1, requester);
        em.persist(itemRequest);
        itemRequest.setId(requestId);

        ItemDto itemDto2 = ItemDto.builder()
                .name("Предмет")
                .description("Описание предмета")
                .available(true)
                .build();
        Item item2 = ItemMapper.dtoToItem(itemDto2, owner2, itemRequest);
        em.persist(item2);
        item2.setId(itemId2);
        em.flush();

        ItemDto itemDto3 = ItemDto.builder()
                .name("Предмет 2")
                .description("Описание предмета 2")
                .available(false)
                .build();
        Item item3 = ItemMapper.dtoToItem(itemDto3, owner2, null);
        em.persist(item3);
        item3.setId(itemId3);
        em.flush();

        LocalDateTime nextBookingStartDate = LocalDateTime.now().plusDays(5);
        LocalDateTime nextBookingEndDate = LocalDateTime.now().plusDays(8);
        AddBookingDto addBookingDto = new AddBookingDto(nextBookingStartDate, nextBookingEndDate, itemId);
        Booking booking = BookingMapper.addBookingDtoToBooking(addBookingDto, requester, item2, BookingStatus.APPROVED);
        em.persist(booking);
        booking.setId(bookingId);

        LocalDateTime lastBookingStartDate = LocalDateTime.now().minusDays(5);
        LocalDateTime lastBookingEndDate = LocalDateTime.now().minusDays(1);
        AddBookingDto addBookingDtoLast = new AddBookingDto(lastBookingStartDate, lastBookingEndDate, itemId);
        Booking booking2 = BookingMapper.addBookingDtoToBooking(addBookingDtoLast, requester, item2, BookingStatus.APPROVED);
        em.persist(booking2);
        booking2.setId(bookingId2);

        LocalDateTime firstCommentDate = LocalDateTime.now().minusDays(1);
        String commentText = "Комментарий 1";
        CommentDto commentDto = new CommentDto(commentText);
        Comment comment = ItemMapper.dtoToComment(commentDto, item2, requester, firstCommentDate);
        em.persist(comment);
        comment.setId(commentId);

        int from = 0;
        int size = 10;
        List<ItemDtoWithBooking> items = itemService.findAllItemsByOwnerId(owner2.getId(), from, size);

        assertThat(items, notNullValue());
        assertThat(items, hasSize(2));
        assertThat(items.get(0).getId(), equalTo(itemId2));
        assertThat(items.get(0).getName(), equalTo(itemDto2.getName()));
        assertThat(items.get(0).getDescription(), equalTo(itemDto2.getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(itemDto2.getAvailable()));
        assertThat(items.get(0).getRequestId(), equalTo(item2.getRequest().getId()));

        assertThat(items.get(0).getLastBooking(), notNullValue());
        assertThat(items.get(0).getLastBooking().getId(), equalTo(bookingId2));
        assertThat(items.get(0).getLastBooking().getItemId(), equalTo(itemId2));
        assertThat(items.get(0).getLastBooking().getStart(), equalTo(lastBookingStartDate));
        assertThat(items.get(0).getLastBooking().getEnd(), equalTo(lastBookingEndDate));
        assertThat(items.get(0).getLastBooking().getBookerId(), equalTo(booking.getBooker().getId()));

        assertThat(items.get(0).getNextBooking(), notNullValue());
        assertThat(items.get(0).getNextBooking().getId(), equalTo(bookingId));
        assertThat(items.get(0).getNextBooking().getItemId(), equalTo(itemId2));
        assertThat(items.get(0).getNextBooking().getStart(), equalTo(nextBookingStartDate));
        assertThat(items.get(0).getNextBooking().getEnd(), equalTo(nextBookingEndDate));
        assertThat(items.get(0).getNextBooking().getBookerId(), equalTo(booking.getBooker().getId()));
        assertThat(items.get(0).getComments(), notNullValue());
        assertThat(items.get(0).getComments(), hasSize(1));
        assertThat(items.get(0).getComments().get(0).getText(), equalTo(commentText));
        assertThat(items.get(0).getComments().get(0).getAuthorName(), equalTo(requester.getName()));
        assertThat(items.get(0).getComments().get(0).getCreated(), equalTo(firstCommentDate));
        assertThat(items.get(1).getId(), equalTo(itemId3));
        assertThat(items.get(1).getName(), equalTo(itemDto3.getName()));
        assertThat(items.get(1).getDescription(), equalTo(itemDto3.getDescription()));
        assertThat(items.get(1).getAvailable(), equalTo(itemDto3.getAvailable()));
    }

    @Test
    void findAllItemsByOwnerId_withoutBookings() {
        long ownerId = 1L;
        long ownerId2 = 2L;
        long requesterId = 3L;
        long itemId = 1L;
        long itemId2 = 2L;
        long itemId3 = 3L;
        long requestId = 1L;

        UserDto ownerDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();

        UserDto ownerDto2 = UserDto.builder()
                .name("John Dolmayan")
                .email("johndolmayan@mail.com")
                .build();

        UserDto requesterDto = UserDto.builder()
                .name("Daron Malakian")
                .email("daronmalakian@mail.com")
                .build();

        User owner = UserMapper.dtoToUser(ownerDto);
        em.persist(owner);
        owner.setId(ownerId);
        User owner2 = UserMapper.dtoToUser(ownerDto2);
        em.persist(owner2);
        owner2.setId(ownerId2);
        em.flush();
        User requester = UserMapper.dtoToUser(requesterDto);
        em.persist(requester);
        requester.setId(requesterId);
        em.flush();

        ItemDto itemDto1 = ItemDto.builder()
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .build();
        Item item = ItemMapper.dtoToItem(itemDto1, owner, null);
        em.persist(item);
        item.setId(itemId);
        em.flush();

        AddItemRequestDto addItemRequestDto1 = new AddItemRequestDto("Необходим предмет 2");
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(addItemRequestDto1, requester);
        em.persist(itemRequest);
        itemRequest.setId(requestId);

        ItemDto itemDto2 = ItemDto.builder()
                .name("Предмет")
                .description("Описание предмета")
                .available(true)
                .build();
        Item item2 = ItemMapper.dtoToItem(itemDto2, owner2, itemRequest);
        em.persist(item2);
        item2.setId(itemId2);
        em.flush();

        ItemDto itemDto3 = ItemDto.builder()
                .name("Предмет 2")
                .description("Описание предмета 2")
                .available(false)
                .build();
        Item item3 = ItemMapper.dtoToItem(itemDto3, owner2, null);
        em.persist(item3);
        item3.setId(itemId3);
        em.flush();

        int from = 0;
        int size = 10;
        List<ItemDtoWithBooking> items = itemService.findAllItemsByOwnerId(owner2.getId(), from, size);

        assertThat(items, notNullValue());
        assertThat(items, hasSize(2));
        assertThat(items.get(0).getId(), equalTo(itemId2));
        assertThat(items.get(0).getName(), equalTo(itemDto2.getName()));
        assertThat(items.get(0).getDescription(), equalTo(itemDto2.getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(itemDto2.getAvailable()));
        assertThat(items.get(0).getRequestId(), equalTo(item2.getRequest().getId()));

        assertThat(items.get(0).getLastBooking(), nullValue());

        assertThat(items.get(0).getNextBooking(), nullValue());
        assertThat(items.get(0).getComments(), notNullValue());
        assertThat(items.get(0).getComments(), hasSize(0));
        assertThat(items.get(1).getId(), equalTo(itemId3));
        assertThat(items.get(1).getName(), equalTo(itemDto3.getName()));
        assertThat(items.get(1).getDescription(), equalTo(itemDto3.getDescription()));
        assertThat(items.get(1).getAvailable(), equalTo(itemDto3.getAvailable()));
    }

    @Test
    void deleteItem() {
        long itemId = 1L;
        ItemDto itemDtoOld = ItemDto.builder()
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .build();

        UserDto userDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();

        User user = UserMapper.dtoToUser(userDto);
        em.persist(user);
        user.setId(1L);
        em.flush();

        em.persist(ItemMapper.dtoToItem(itemDtoOld, user, null));
        em.flush();

        itemService.deleteItem(itemId);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);

        final NoResultException exception = assertThrows(
                NoResultException.class,
                () -> query.setParameter("id", itemId)
                        .getSingleResult()
        );

        assertEquals("No entity found for query", exception.getMessage());
    }

    @Test
    void searchByText() {
        long ownerId = 1L;
        long ownerId2 = 2L;
        long itemId = 1L;
        long itemId2 = 2L;
        long itemId3 = 3L;

        UserDto ownerDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();

        UserDto ownerDto2 = UserDto.builder()
                .name("John Dolmayan")
                .email("johndolmayan@mail.com")
                .build();

        User owner = UserMapper.dtoToUser(ownerDto);
        em.persist(owner);
        owner.setId(ownerId);
        User owner2 = UserMapper.dtoToUser(ownerDto2);
        em.persist(owner2);
        owner2.setId(ownerId2);
        em.flush();

        ItemDto itemDto1 = ItemDto.builder()
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .build();
        Item item = ItemMapper.dtoToItem(itemDto1, owner, null);
        em.persist(item);
        item.setId(itemId);
        em.flush();

        ItemDto itemDto2 = ItemDto.builder()
                .name("Предмет")
                .description("Описание предмета")
                .available(true)
                .build();
        Item item2 = ItemMapper.dtoToItem(itemDto2, owner2, null);
        em.persist(item2);
        item2.setId(itemId2);
        em.flush();

        ItemDto itemDto3 = ItemDto.builder()
                .name("Предмет 2")
                .description("Описание предмета 2")
                .available(false)
                .build();
        Item item3 = ItemMapper.dtoToItem(itemDto3, owner2, null);
        em.persist(item3);
        item3.setId(itemId3);
        em.flush();

        String text = "Описание";
        int from = 0;
        int size = 10;
        List<ItemDto> itemDtoList = itemService.searchByText(text, from, size);

        assertThat(itemDtoList, hasSize(2));
        assertThat(itemDtoList.get(0).getId(), equalTo(itemId));
        assertThat(itemDtoList.get(1).getId(), equalTo(itemId2));
    }

    @Test
    void addComment() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        long commentId = 1L;

        UserDto ownerDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();

        UserDto bookerDto = UserDto.builder()
                .name("John Dolmayan")
                .email("johndolmayan@mail.com")
                .build();

        User owner = UserMapper.dtoToUser(ownerDto);
        em.persist(owner);
        owner.setId(ownerId);
        User booker = UserMapper.dtoToUser(bookerDto);
        em.persist(booker);
        booker.setId(bookerId);
        em.flush();

        ItemDto itemDtoOld = ItemDto.builder()
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .build();
        Item item = ItemMapper.dtoToItem(itemDtoOld, owner, null);
        em.persist(item);
        item.setId(itemId);
        em.flush();

        AddBookingDto bookingDto = new AddBookingDto(
                LocalDateTime.parse("2023-06-01T00:00:00", dateTimeFormatter),
                LocalDateTime.parse("2023-06-02T00:00:00", dateTimeFormatter),
                ownerId);
        Booking booking = BookingMapper.addBookingDtoToBooking(bookingDto, booker, item, BookingStatus.APPROVED);
        em.persist(booking);
        booking.setId(bookingId);

        final String text = "Комментарий";
        CommentDto commentDto = new CommentDto(text);
        LocalDateTime start = LocalDateTime.now().minusSeconds(1);

        itemService.addComment(commentDto, bookerId, itemId);

        LocalDateTime end = LocalDateTime.now().plusSeconds(1);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment comment = query.setParameter("id", commentId)
                .getSingleResult();

        assertThat(comment, notNullValue());
        assertThat(comment.getId(), equalTo(commentId));
        assertThat(comment.getText(), equalTo(text));
        assertThat(comment.getCreated(), greaterThan(start));
        assertThat(comment.getCreated(), lessThan(end));
        assertThat(comment.getAuthor(), equalTo(booker));
        assertThat(comment.getItem(), equalTo(item));
    }
}