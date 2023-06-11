package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class BookingServiceImplJunitTest {
    private static final long userId = 1L;
    private static final long userId2 = 2L;
    private static final long userId3 = 3L;
    private static final long itemId = 1L;
    private static final long bookingId = 1L;

    private final LocalDateTime bookingStartDate = LocalDateTime.now();
    private final LocalDateTime bookingEndDate = bookingStartDate.plusDays(1);
    private final AddBookingDto addBookingDto = new AddBookingDto(bookingStartDate, bookingEndDate, itemId);

    @Mock
    BookingDao bookingDao;
    @Mock
    UserDao userDao;
    @Mock
    ItemDao itemDao;

    @Test
    void createBooking_whenUserNotFound_thenThrowUserNotFoundException() {
        BookingService bookingService = new BookingServiceImpl(bookingDao, userDao, itemDao);

        when(userDao.findById(userId)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.createBooking(addBookingDto, userId)
        );

        assertEquals("Пользователь с идентификатором = " + userId + " не найден.", exception.getMessage());
    }

    @Test
    void createBooking_whenItemNotFound_thenThrowItemNotFoundException() {
        BookingService bookingService = new BookingServiceImpl(bookingDao, userDao, itemDao);

        when(userDao.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(itemDao.findById(itemId)).thenReturn(Optional.empty());

        final ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> bookingService.createBooking(addBookingDto, userId)
        );

        assertEquals("Предмет с идентификатором " + itemId + " не найден.", exception.getMessage());
    }

    @Test
    void createBooking_whenItemIsNotAvailable_thenThrowValidationException() {
        BookingService bookingService = new BookingServiceImpl(bookingDao, userDao, itemDao);

        when(userDao.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(itemDao.findById(itemId)).thenReturn(Optional.of(Item.builder().available(false).build()));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(addBookingDto, userId)
        );

        assertEquals("Бронирование недоступной вещи запрещено.", exception.getMessage());
    }

    @Test
    void createBooking_whenBookerIsOwner_thenThrowValidationException() {
        BookingService bookingService = new BookingServiceImpl(bookingDao, userDao, itemDao);

        when(userDao.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));

        User owner = User.builder().id(userId).build();
        Item item = Item.builder().available(true).owner(owner).build();
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.createBooking(addBookingDto, userId)
        );

        assertEquals("Владелец не может бронировать собственные вещи", exception.getMessage());
    }

    @Test
    void getById_whenUserNotFound_thenThrowUserNotFoundException() {
        BookingService bookingService = new BookingServiceImpl(bookingDao, userDao, itemDao);

        Booking booking = Booking.builder().build();
        when(bookingDao.findById(bookingId)).thenReturn(Optional.of(booking));

        when(userDao.findById(userId)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.getById(bookingId, userId)
        );

        assertEquals("Пользователь с идентификатором = " + userId + " не найден.", exception.getMessage());
    }

    @Test
    void getById_whenBookingNotFound_thenThrowBookingNotFoundException() {
        BookingService bookingService = new BookingServiceImpl(bookingDao, userDao, itemDao);

        when(bookingDao.findById(bookingId)).thenReturn(Optional.empty());

        final BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getById(bookingId, userId)
        );

        assertEquals("Бронирование с идентификатором " + bookingId + " не найдено.", exception.getMessage());
    }

    @Test
    void getById_whenUserIsNotBookerOrOwner_thenThrowUserNotFoundException() {
        BookingService bookingService = new BookingServiceImpl(bookingDao, userDao, itemDao);

        User booker = User.builder().id(userId).build();
        User owner = User.builder().id(userId2).build();
        Item item = Item.builder().owner(owner).build();
        Booking booking = Booking.builder().item(item).booker(booker).build();
        when(bookingDao.findById(bookingId)).thenReturn(Optional.of(booking));

        long wrongUserId = userId + userId2;
        User wrongUser = User.builder().id(wrongUserId).build();
        when(userDao.findById(wrongUserId)).thenReturn(Optional.of(wrongUser));

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.getById(bookingId, wrongUserId)
        );

        assertEquals("Получать информацию о бронировании может только его автор или владелец вещи.",
                exception.getMessage());
    }

    @Test
    void approve_whenStatusIsApproved_thenThrowValidationException() {
        BookingService bookingService = new BookingServiceImpl(bookingDao, userDao, itemDao);

        Booking booking = Booking.builder().status(BookingStatus.APPROVED).build();
        when(bookingDao.findById(bookingId)).thenReturn(Optional.of(booking));

        boolean approved = true;

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.approve(bookingId, userId, approved)
        );

        assertEquals("Статус бронирования нельзя изменить после подтверждения.",
                exception.getMessage());
    }

    @Test
    void approved_whenBookerIdEqualsOwnerId_thenThrowValidationException() {
        BookingService bookingService = new BookingServiceImpl(bookingDao, userDao, itemDao);

        User booker = User.builder().id(userId).build();
        Booking booking = Booking.builder().booker(booker).status(BookingStatus.WAITING).build();
        when(bookingDao.findById(bookingId)).thenReturn(Optional.of(booking));

        User owner = User.builder().id(userId).build();
        when(userDao.findById(userId)).thenReturn(Optional.of(owner));

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.approve(bookingId, userId, true)
        );

        assertEquals("Подтвердить или отклонить бронирование может только владелец вещи.",
                exception.getMessage());
    }

    @Test
    void approved_whenUserIdIsNotOwnerId_thenThrowValidationException() {
        BookingService bookingService = new BookingServiceImpl(bookingDao, userDao, itemDao);

        User booker = User.builder().id(userId3).build();
        User ownerOfItem = User.builder().id(userId2).build();
        Item item = Item.builder().owner(ownerOfItem).build();
        Booking booking = Booking.builder().booker(booker).item(item).status(BookingStatus.WAITING).build();
        when(bookingDao.findById(bookingId)).thenReturn(Optional.of(booking));

        User owner = User.builder().id(userId).build();
        when(userDao.findById(userId)).thenReturn(Optional.of(owner));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.approve(bookingId, userId, true)
        );

        assertEquals("Подтвердить или отклонить бронирование может только владелец вещи.",
                exception.getMessage());
    }

    @Test
    void approved_whenRejected() {
        BookingService bookingService = new BookingServiceImpl(bookingDao, userDao, itemDao);

        User booker = User.builder().id(userId3).build();
        User ownerOfItem = User.builder().id(userId).build();
        Item item = Item.builder().owner(ownerOfItem).build();
        Booking booking = Booking.builder().booker(booker).item(item).status(BookingStatus.WAITING).build();
        when(bookingDao.findById(bookingId)).thenReturn(Optional.of(booking));

        User owner = User.builder().id(userId).build();
        when(userDao.findById(userId)).thenReturn(Optional.of(owner));

        when(bookingDao.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GetBookingDto bookingDto = bookingService.approve(bookingId, userId, false);

        assertThat(bookingDto.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void approve_whenBookingNotFound_thenThrowBookingNotFoundException() {
        BookingService bookingService = new BookingServiceImpl(bookingDao, userDao, itemDao);

        when(bookingDao.findById(bookingId)).thenReturn(Optional.empty());

        boolean approve = true;
        final BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.approve(bookingId, userId, approve)
        );

        assertEquals("Бронирование с идентификатором " + bookingId + " не найдено.", exception.getMessage());
    }

    @Test
    void approved_whenUserNotFound_thenThrowUserNotFoundException() {
        BookingService bookingService = new BookingServiceImpl(bookingDao, userDao, itemDao);

        Booking booking = Booking.builder().status(BookingStatus.WAITING).build();
        when(bookingDao.findById(bookingId)).thenReturn(Optional.of(booking));

        when(userDao.findById(userId)).thenReturn(Optional.empty());

        boolean approve = true;
        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.approve(bookingId, userId, approve)
        );

        assertEquals("Пользователь с идентификатором = " + userId + " не найден.", exception.getMessage());
    }

    @Test
    void findAllByOwnerId_whenUserNotFound_thenThrowUserNotFoundException() {
        BookingService bookingService = new BookingServiceImpl(bookingDao, userDao, itemDao);

        when(userDao.findById(userId)).thenReturn(Optional.empty());

        int from = 0;
        int size = 10;
        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.findBookingsByOwnerId(userId, BookingState.ALL, from, size)
        );

        assertEquals("Пользователь с идентификатором = " + userId + " не найден.", exception.getMessage());
    }

    @Test
    void findAllByBookerId_whenUserNotFound_thenThrowUserNotFoundException() {
        BookingService bookingService = new BookingServiceImpl(bookingDao, userDao, itemDao);

        when(userDao.findById(userId)).thenReturn(Optional.empty());

        int from = 0;
        int size = 10;
        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.findBookingsByBookerId(userId, BookingState.ALL, from, size)
        );

        assertEquals("Пользователь с идентификатором = " + userId + " не найден.", exception.getMessage());
    }
}