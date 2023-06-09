package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingDao bookingDao;
    private final UserDao userDao;
    private final ItemDao itemDao;

    @Override
    public GetBookingDto createBooking(AddBookingDto addBookingDto, long userId) {
        User booker = checkUserAndGet(userId);

        Item item = checkItemAndGet(addBookingDto.getItemId());

        if (!item.getAvailable()) {
            throw new ValidationException("Бронирование недоступной вещи запрещено.");
        }
        if (item.getOwner().getId().equals(booker.getId())) {
            throw new UserNotFoundException("Владелец не может бронировать собственные вещи");
        }

        Booking booking = BookingMapper.addBookingDtoToBooking(addBookingDto, booker, item, BookingStatus.WAITING);
        Booking savedBooking = bookingDao.save(booking);
        GetBookingDto getBookingDto = BookingMapper.bookingToDto(savedBooking);
        return getBookingDto;
    }

    @Override
    public GetBookingDto getById(long bookingId, long userId) {
        Booking booking = checkBookingAndGet(bookingId);

        User ownerOrBooker = checkUserAndGet(userId);

        if (!Objects.equals(ownerOrBooker.getId(), booking.getBooker().getId()) &&
                !Objects.equals(ownerOrBooker.getId(), booking.getItem().getOwner().getId())) {
            throw new UserNotFoundException("Получать информацию о бронировании может только его автор или владелец вещи.");
        }

        GetBookingDto getBookingDto = BookingMapper.bookingToDto(booking);
        return getBookingDto;
    }

    @Override
    public GetBookingDto approve(long bookingId, long userId, boolean approved) {
        Booking booking = checkBookingAndGet(bookingId);

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Статус бронирования нельзя изменить после подтверждения.");
        }

        User owner = checkUserAndGet(userId);

        if (Objects.equals(booking.getBooker().getId(), owner.getId())) {
            throw new UserNotFoundException("Подтвердить или отклонить бронирование может только владелец вещи.");
        }
        if (!Objects.equals(booking.getItem().getOwner().getId(), owner.getId())) {
            throw new ValidationException("Подтвердить или отклонить бронирование может только владелец вещи.");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking savedBooking = bookingDao.save(booking);
        GetBookingDto getBookingDto = BookingMapper.bookingToDto(savedBooking);
        return getBookingDto;
    }

    @Override
    public List<GetBookingDto> findBookingsByBookerId(long bookerId, BookingState state, int from, int size) {
        User booker = checkUserAndGet(bookerId);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.DESC, "start"));
        BooleanExpression byBookerId = QBooking.booking.booker.id.eq(booker.getId());
        BooleanExpression byAnyState = createStatePredicate(state);
        Iterable<Booking> foundBookings = bookingDao.findAll(byBookerId.and(byAnyState), page);
        List<GetBookingDto> result = BookingMapper.bookingListToDto(foundBookings);
        return result;
    }

    @Override
    public List<GetBookingDto> findBookingsByOwnerId(long ownerId, BookingState state, int from, int size) {
        User booker = checkUserAndGet(ownerId);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.DESC, "start"));
        BooleanExpression byOwnerId = QBooking.booking.item.owner.id.eq(booker.getId());
        BooleanExpression byAnyState = createStatePredicate(state);
        Iterable<Booking> foundBookings = bookingDao.findAll(byOwnerId.and(byAnyState), page);
        List<GetBookingDto> result = BookingMapper.bookingListToDto(foundBookings);
        return result;
    }

    private User checkUserAndGet(long userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором = " + userId + " не найден."));

    }

    private Item checkItemAndGet(long itemId) {
        return itemDao.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет с идентификатором " + itemId + " не найден."));
    }

    private Booking checkBookingAndGet(long bookingId) {
        return bookingDao.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с идентификатором " + bookingId + " не найдено."));
    }

    private BooleanExpression createStatePredicate(BookingState state) {
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case CURRENT:
                return QBooking.booking.status.in(BookingStatus.APPROVED, BookingStatus.REJECTED)
                        .and(QBooking.booking.start.before(now))
                        .and(QBooking.booking.end.after(now));
            case PAST:
                return QBooking.booking.status.in(BookingStatus.CANCELED, BookingStatus.APPROVED)
                        .and(QBooking.booking.end.before(now));
            case FUTURE:
                return QBooking.booking.status.in(BookingStatus.APPROVED, BookingStatus.WAITING)
                        .and(QBooking.booking.start.after(now));
            case WAITING:
                return QBooking.booking.status.in(BookingStatus.WAITING)
                        .and(QBooking.booking.start.after(now));
            case REJECTED:
                return QBooking.booking.status.in(BookingStatus.REJECTED);
        }

        return QBooking.booking.status.in(BookingStatus.APPROVED, BookingStatus.CANCELED, BookingStatus.REJECTED, BookingStatus.WAITING);
    }

}
