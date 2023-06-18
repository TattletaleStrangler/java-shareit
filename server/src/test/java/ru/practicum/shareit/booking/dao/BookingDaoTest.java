package ru.practicum.shareit.booking.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingDaoTest {

    private static final long ITEM_ID1 = 1;
    private static final long ITEM_ID2 = 2;
    private static final long ITEM_ID3 = 3;
    private static final long OWNER_ID1 = 1;
    private static final long OWNER_ID2 = 2;
    private static final long REQUESTER_ID1 = 3;
    private static final long REQUESTER_ID2 = 4;
    private static final long BOOKER_ID1 = 5;
    private static final long BOOKER_ID2 = 6;
    private static final long BOOKING_ID1 = 1;
    private static final long BOOKING_ID2 = 2;
    private static final long BOOKING_ID3 = 3;
    private static final long BOOKING_ID4 = 4;
    private static final long BOOKING_ID5 = 5;
    private static final long BOOKING_ID6 = 6;
    private static final long COMMENT_ID1 = 1;
    private static final long COMMENT_ID2 = 2;
    private static final long COMMENT_ID3 = 3;

    private static final long REQUEST_ID1 = 1;
    private static final long REQUEST_ID2 = 2;
    private static final long REQUEST_ID3 = 3;

    private User owner1;
    private User owner2;
    private User requester1;
    private User requester2;
    private User booker1;
    private User booker2;
    private Item item1;
    private Item item2;
    private Item item3;

    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    private Booking booking5;
    private Booking booking6;
    private Comment comment1;
    private Comment comment2;
    private Comment comment3;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private ItemRequest itemRequest3;
    private final TestEntityManager em;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final BookingDao bookingDao;

    @BeforeEach
    void setUp() {
        UserDto ownerDto = UserDto.builder()
                .name("Owner1")
                .email("owner1@yandex.ru")
                .build();
        owner1 = UserMapper.dtoToUser(ownerDto);
        em.persist(owner1);
        owner1.setId(OWNER_ID1);
        em.flush();

        UserDto ownerDto2 = UserDto.builder()
                .name("Owner2")
                .email("owner2@yandex.ru")
                .build();
        owner2 = UserMapper.dtoToUser(ownerDto2);
        em.persist(owner2);
        owner2.setId(OWNER_ID2);
        em.flush();

        UserDto requesterDto1 = UserDto.builder()
                .name("Requester1")
                .email("requester1@yandex.ru")
                .build();
        requester1 = UserMapper.dtoToUser(requesterDto1);
        em.persist(requester1);
        requester1.setId(REQUESTER_ID1);
        em.flush();

        UserDto requesterDto2 = UserDto.builder()
                .name("Requester2")
                .email("requester2@yandex.ru")
                .build();
        requester2 = UserMapper.dtoToUser(requesterDto2);
        em.persist(requester2);
        requester2.setId(REQUESTER_ID2);
        em.flush();

        UserDto bookerDto1 = UserDto.builder()
                .name("Booker 1")
                .email("booker1@yandex.ru")
                .build();
        booker1 = UserMapper.dtoToUser(bookerDto1);
        em.persist(booker1);
        booker1.setId(BOOKER_ID1);
        em.flush();

        UserDto bookerDto2 = UserDto.builder()
                .name("Booker 2")
                .email("booker2@yandex.ru")
                .build();
        booker2 = UserMapper.dtoToUser(bookerDto2);
        em.persist(booker2);
        booker2.setId(BOOKER_ID2);
        em.flush();

        AddItemRequestDto addItemRequestDto1 = new AddItemRequestDto("Необходима вещь");
        itemRequest1 = ItemRequestMapper.dtoToItemRequest(addItemRequestDto1, requester1);
        em.persist(itemRequest1);
        itemRequest1.setId(REQUEST_ID1);

        ItemDto itemDto = ItemDto.builder()
                .name("Item1 owner1 itemRequest1")
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
                .name("Item2 owner2 itemRequest2")
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
                .name("Item3 owner2")
                .description("Подробности о вещи 3")
                .available(true)
                .build();
        item3 = ItemMapper.dtoToItem(itemDto3, owner2, null);
        em.persist(item3);
        item3.setId(ITEM_ID3);
        em.flush();

        LocalDateTime bookingStartDate1 = LocalDateTime.now().minusDays(10);
        LocalDateTime bookingEndDate1 = LocalDateTime.now().minusDays(5);
        AddBookingDto addBookingDto = new AddBookingDto(bookingStartDate1, bookingEndDate1, ITEM_ID1);
        booking1 = BookingMapper.addBookingDtoToBooking(addBookingDto, booker1, item1, BookingStatus.APPROVED);
        em.persist(booking1);
        booking1.setId(BOOKING_ID1);
        em.flush();

        LocalDateTime bookingStartDate2 = LocalDateTime.now().minusDays(2);
        LocalDateTime bookingEndDate2 = LocalDateTime.now().plusDays(5);
        AddBookingDto addBookingDto2 = new AddBookingDto(bookingStartDate2, bookingEndDate2, ITEM_ID2);
        booking2 = BookingMapper.addBookingDtoToBooking(addBookingDto2, booker1, item2, BookingStatus.APPROVED);
        em.persist(booking2);
        booking2.setId(BOOKING_ID2);
        em.flush();

        LocalDateTime bookingStartDate3 = LocalDateTime.now().plusDays(7);
        LocalDateTime bookingEndDate3 = LocalDateTime.now().plusDays(10);
        AddBookingDto addBookingDto3 = new AddBookingDto(bookingStartDate3, bookingEndDate3, ITEM_ID3);
        booking3 = BookingMapper.addBookingDtoToBooking(addBookingDto3, booker1, item3, BookingStatus.REJECTED);
        em.persist(booking3);
        booking3.setId(BOOKING_ID3);
        em.flush();

        LocalDateTime bookingStartDate4 = LocalDateTime.now().minusDays(1);
        LocalDateTime bookingEndDate4 = LocalDateTime.now().plusDays(7);
        AddBookingDto addBookingDto4 = new AddBookingDto(bookingStartDate4, bookingEndDate4, ITEM_ID1);
        booking4 = BookingMapper.addBookingDtoToBooking(addBookingDto4, booker2, item1, BookingStatus.APPROVED);
        em.persist(booking4);
        booking4.setId(BOOKING_ID4);
        em.flush();

        LocalDateTime bookingStartDate5 = LocalDateTime.now().plusDays(10);
        LocalDateTime bookingEndDate5 = LocalDateTime.now().plusDays(15);
        AddBookingDto addBookingDto5 = new AddBookingDto(bookingStartDate5, bookingEndDate5, ITEM_ID1);
        booking5 = BookingMapper.addBookingDtoToBooking(addBookingDto5, booker2, item1, BookingStatus.APPROVED);
        em.persist(booking5);
        booking5.setId(BOOKING_ID5);
        em.flush();

        LocalDateTime bookingStartDate6 = LocalDateTime.now().plusDays(7);
        LocalDateTime bookingEndDate6 = LocalDateTime.now().plusDays(10);
        AddBookingDto addBookingDto6 = new AddBookingDto(bookingStartDate6, bookingEndDate6, ITEM_ID2);
        booking6 = BookingMapper.addBookingDtoToBooking(addBookingDto6, booker2, item2, BookingStatus.WAITING);
        em.persist(booking6);
        booking6.setId(BOOKING_ID6);
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
    void findFirstByItemIdAndStatusAndStartLessThanEqualOrderByStartDesc() {
        LocalDateTime date = LocalDateTime.now();
        Booking booking = bookingDao.findFirstByItemIdAndStatusAndStartLessThanEqualOrderByStartDesc(ITEM_ID1, BookingStatus.APPROVED, date);
        assertEquals(booking4, booking);
    }

    @Test
    void findLastByItems() {
        List<Item> items = List.of(item1, item2);
        LocalDateTime date = LocalDateTime.now();
        List<Booking> bookings = bookingDao.findLastByItems(items, BookingStatus.APPROVED, date);

        assertThat(bookings, notNullValue());
        assertThat(bookings, hasSize(3));
        assertThat(bookings.get(0).getId(), equalTo(booking4.getId()));
        assertThat(bookings.get(0).getStart(), equalTo(booking4.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(booking4.getEnd()));
        assertThat(bookings.get(0).getStatus(), equalTo(booking4.getStatus()));
        assertThat(bookings.get(0).getItem(), notNullValue());
        assertThat(bookings.get(0).getItem().getId(), equalTo(booking4.getItem().getId()));
        assertThat(bookings.get(0).getItem().getName(), equalTo(booking4.getItem().getName()));
        assertThat(bookings.get(0).getItem().getDescription(), equalTo(booking4.getItem().getDescription()));
        assertThat(bookings.get(0).getItem().getAvailable(), equalTo(booking4.getItem().getAvailable()));
        assertThat(bookings.get(0).getItem().getOwner(), notNullValue());
        assertThat(bookings.get(0).getItem().getOwner().getId(), equalTo(booking4.getItem().getOwner().getId()));
        assertThat(bookings.get(0).getItem().getOwner().getName(), equalTo(booking4.getItem().getOwner().getName()));
        assertThat(bookings.get(0).getItem().getOwner().getEmail(), equalTo(booking4.getItem().getOwner().getEmail()));
        assertThat(bookings.get(0).getBooker(), notNullValue());
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booking4.getBooker().getId()));
        assertThat(bookings.get(0).getBooker().getName(), equalTo(booking4.getBooker().getName()));
        assertThat(bookings.get(0).getBooker().getEmail(), equalTo(booking4.getBooker().getEmail()));

        assertThat(bookings.get(1).getId(), equalTo(booking2.getId()));
        assertThat(bookings.get(1).getStart(), equalTo(booking2.getStart()));
        assertThat(bookings.get(1).getEnd(), equalTo(booking2.getEnd()));
        assertThat(bookings.get(1).getStatus(), equalTo(booking2.getStatus()));
        assertThat(bookings.get(1).getItem(), notNullValue());
        assertThat(bookings.get(1).getItem().getId(), equalTo(booking2.getItem().getId()));
        assertThat(bookings.get(1).getItem().getName(), equalTo(booking2.getItem().getName()));
        assertThat(bookings.get(1).getItem().getDescription(), equalTo(booking2.getItem().getDescription()));
        assertThat(bookings.get(1).getItem().getAvailable(), equalTo(booking2.getItem().getAvailable()));
        assertThat(bookings.get(1).getItem().getOwner(), notNullValue());
        assertThat(bookings.get(1).getItem().getOwner().getId(), equalTo(booking2.getItem().getOwner().getId()));
        assertThat(bookings.get(1).getItem().getOwner().getName(), equalTo(booking2.getItem().getOwner().getName()));
        assertThat(bookings.get(1).getItem().getOwner().getEmail(), equalTo(booking2.getItem().getOwner().getEmail()));
        assertThat(bookings.get(1).getBooker(), notNullValue());
        assertThat(bookings.get(1).getBooker().getId(), equalTo(booking2.getBooker().getId()));
        assertThat(bookings.get(1).getBooker().getName(), equalTo(booking2.getBooker().getName()));
        assertThat(bookings.get(1).getBooker().getEmail(), equalTo(booking2.getBooker().getEmail()));

        assertThat(bookings.get(2).getId(), equalTo(booking1.getId()));
        assertThat(bookings.get(2).getStart(), equalTo(booking1.getStart()));
        assertThat(bookings.get(2).getEnd(), equalTo(booking1.getEnd()));
        assertThat(bookings.get(2).getStatus(), equalTo(booking1.getStatus()));
        assertThat(bookings.get(2).getItem(), notNullValue());
        assertThat(bookings.get(2).getItem().getId(), equalTo(booking1.getItem().getId()));
        assertThat(bookings.get(2).getItem().getName(), equalTo(booking1.getItem().getName()));
        assertThat(bookings.get(2).getItem().getDescription(), equalTo(booking1.getItem().getDescription()));
        assertThat(bookings.get(2).getItem().getAvailable(), equalTo(booking1.getItem().getAvailable()));
        assertThat(bookings.get(2).getItem().getOwner(), notNullValue());
        assertThat(bookings.get(2).getItem().getOwner().getId(), equalTo(booking1.getItem().getOwner().getId()));
        assertThat(bookings.get(2).getItem().getOwner().getName(), equalTo(booking1.getItem().getOwner().getName()));
        assertThat(bookings.get(2).getItem().getOwner().getEmail(), equalTo(booking1.getItem().getOwner().getEmail()));
        assertThat(bookings.get(2).getBooker(), notNullValue());
        assertThat(bookings.get(2).getBooker().getId(), equalTo(booking1.getBooker().getId()));
        assertThat(bookings.get(2).getBooker().getName(), equalTo(booking1.getBooker().getName()));
        assertThat(bookings.get(2).getBooker().getEmail(), equalTo(booking1.getBooker().getEmail()));

    }

    @Test
    void findNextByItems() {
        List<Item> items = List.of(item1, item2);
        LocalDateTime date = LocalDateTime.now().minusDays(2);
        List<Booking> bookings = bookingDao.findNextByItems(List.of(item1, item2), BookingStatus.APPROVED, date);

        assertThat(bookings, notNullValue());
        assertThat(bookings, hasSize(2));
        assertThat(bookings.get(0).getId(), equalTo(booking4.getId()));
        assertThat(bookings.get(0).getStart(), equalTo(booking4.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(booking4.getEnd()));
        assertThat(bookings.get(0).getStatus(), equalTo(booking4.getStatus()));
        assertThat(bookings.get(0).getItem(), notNullValue());
        assertThat(bookings.get(0).getItem().getId(), equalTo(booking4.getItem().getId()));
        assertThat(bookings.get(0).getItem().getName(), equalTo(booking4.getItem().getName()));
        assertThat(bookings.get(0).getItem().getDescription(), equalTo(booking4.getItem().getDescription()));
        assertThat(bookings.get(0).getItem().getAvailable(), equalTo(booking4.getItem().getAvailable()));
        assertThat(bookings.get(0).getItem().getOwner(), notNullValue());
        assertThat(bookings.get(0).getItem().getOwner().getId(), equalTo(booking4.getItem().getOwner().getId()));
        assertThat(bookings.get(0).getItem().getOwner().getName(), equalTo(booking4.getItem().getOwner().getName()));
        assertThat(bookings.get(0).getItem().getOwner().getEmail(), equalTo(booking4.getItem().getOwner().getEmail()));
        assertThat(bookings.get(0).getBooker(), notNullValue());
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booking4.getBooker().getId()));
        assertThat(bookings.get(0).getBooker().getName(), equalTo(booking4.getBooker().getName()));
        assertThat(bookings.get(0).getBooker().getEmail(), equalTo(booking4.getBooker().getEmail()));

        assertThat(bookings.get(1).getId(), equalTo(booking5.getId()));
        assertThat(bookings.get(1).getStart(), equalTo(booking5.getStart()));
        assertThat(bookings.get(1).getEnd(), equalTo(booking5.getEnd()));
        assertThat(bookings.get(1).getStatus(), equalTo(booking5.getStatus()));
        assertThat(bookings.get(1).getItem(), notNullValue());
        assertThat(bookings.get(1).getItem().getId(), equalTo(booking5.getItem().getId()));
        assertThat(bookings.get(1).getItem().getName(), equalTo(booking5.getItem().getName()));
        assertThat(bookings.get(1).getItem().getDescription(), equalTo(booking5.getItem().getDescription()));
        assertThat(bookings.get(1).getItem().getAvailable(), equalTo(booking5.getItem().getAvailable()));
        assertThat(bookings.get(1).getItem().getOwner(), notNullValue());
        assertThat(bookings.get(1).getItem().getOwner().getId(), equalTo(booking5.getItem().getOwner().getId()));
        assertThat(bookings.get(1).getItem().getOwner().getName(), equalTo(booking5.getItem().getOwner().getName()));
        assertThat(bookings.get(1).getItem().getOwner().getEmail(), equalTo(booking5.getItem().getOwner().getEmail()));
        assertThat(bookings.get(1).getBooker(), notNullValue());
        assertThat(bookings.get(1).getBooker().getId(), equalTo(booking5.getBooker().getId()));
        assertThat(bookings.get(1).getBooker().getName(), equalTo(booking5.getBooker().getName()));
        assertThat(bookings.get(1).getBooker().getEmail(), equalTo(booking5.getBooker().getEmail()));
    }


    @Test
    void findFirstByItemIdAndStatusAndStartGreaterThanOrderByStart() {
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        Booking booking = bookingDao.findFirstByItemIdAndStatusAndStartGreaterThanOrderByStart(ITEM_ID2, BookingStatus.WAITING, date);

        assertThat(booking, notNullValue());
        assertThat(booking.getId(), equalTo(booking6.getId()));
        assertThat(booking.getStart(), equalTo(booking6.getStart()));
        assertThat(booking.getEnd(), equalTo(booking6.getEnd()));
        assertThat(booking.getStatus(), equalTo(booking6.getStatus()));
        assertThat(booking.getItem(), notNullValue());
        assertThat(booking.getItem().getId(), equalTo(booking6.getItem().getId()));
        assertThat(booking.getItem().getName(), equalTo(booking6.getItem().getName()));
        assertThat(booking.getItem().getDescription(), equalTo(booking6.getItem().getDescription()));
        assertThat(booking.getItem().getAvailable(), equalTo(booking6.getItem().getAvailable()));
        assertThat(booking.getItem().getOwner(), notNullValue());
        assertThat(booking.getItem().getOwner().getId(), equalTo(booking6.getItem().getOwner().getId()));
        assertThat(booking.getItem().getOwner().getName(), equalTo(booking6.getItem().getOwner().getName()));
        assertThat(booking.getItem().getOwner().getEmail(), equalTo(booking6.getItem().getOwner().getEmail()));
        assertThat(booking.getBooker(), notNullValue());
        assertThat(booking.getBooker().getId(), equalTo(booking6.getBooker().getId()));
        assertThat(booking.getBooker().getName(), equalTo(booking6.getBooker().getName()));
        assertThat(booking.getBooker().getEmail(), equalTo(booking6.getBooker().getEmail()));
    }

    @Test
    void existsByItemIdAndBookerIdAndStatusAndEndLessThan() {
        LocalDateTime date = LocalDateTime.now();

        assertThat(bookingDao.existsByItemIdAndBookerIdAndStatusAndEndLessThan(ITEM_ID1, booker1.getId(),
                BookingStatus.APPROVED, date), equalTo(true));

        assertThat(bookingDao.existsByItemIdAndBookerIdAndStatusAndEndLessThan(ITEM_ID2, booker1.getId(),
                BookingStatus.APPROVED, date), equalTo(false));

        assertThat(bookingDao.existsByItemIdAndBookerIdAndStatusAndEndLessThan(ITEM_ID3, booker2.getId(),
                BookingStatus.APPROVED, date), equalTo(false));

        assertThat(bookingDao.existsByItemIdAndBookerIdAndStatusAndEndLessThan(ITEM_ID2, booker2.getId(),
                BookingStatus.WAITING, date.plusDays(11)), equalTo(true));
    }
}