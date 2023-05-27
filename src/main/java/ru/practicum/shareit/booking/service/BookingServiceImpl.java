package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSmall;
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
    public BookingDto createBooking(BookingDtoSmall bookingDtoSmall, long userId) {
        User booker = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором = " + userId + " не найден."));
        Item item = itemDao.findById(bookingDtoSmall.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Предмет с идентификатором " + bookingDtoSmall.getItemId() + " не найден."));
        if (!item.getAvailable()) {
            throw new ValidationException("Бронирование недоступной вещи запрещено.");
        }
        if (item.getOwner().getId().equals(booker.getId())) {
            throw new UserNotFoundException("Владелец не может бронировать собственные вещи");
        }

        Booking booking = BookingMapper.dtoSmallToBooking(bookingDtoSmall, booker, item, BookingStatus.WAITING);
        Booking savedBooking = bookingDao.save(booking);
        BookingDto bookingDto = BookingMapper.bookingToDto(savedBooking);
        return bookingDto;
    }

    @Override
    public BookingDto getById(long bookingId, long userId) {
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с идентификатором " + bookingId + " не найдено."));
        User ownerOrBooker = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором = " + userId + " не найден."));

        if (!Objects.equals(ownerOrBooker.getId(), booking.getBooker().getId()) &&
                !Objects.equals(ownerOrBooker.getId(), booking.getItem().getOwner().getId())) {
            throw new UserNotFoundException("Получать информацию о бронировании может только его автор или владелец вещи.");
        }

        BookingDto bookingDto = BookingMapper.bookingToDto(booking);
        return bookingDto;
    }

    @Override
    public BookingDto approve(long bookingId, long userId, boolean approved) {
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с идентификатором " + bookingId + " не найдено."));

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Статус бронирования нельзя изменить после подтверждения.");
        }

        User owner = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором = " + userId + " не найден."));

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
        BookingDto bookingDto = BookingMapper.bookingToDto(savedBooking);
        return bookingDto;
    }

    @Override
    public List<BookingDto> findBookingsByBookerId(long bookerId, BookingState state) {
        User booker = userDao.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором = " + bookerId + " не найден."));

        BooleanExpression byBookerId = QBooking.booking.booker.id.eq(booker.getId());
        BooleanExpression byAnyState = createStatePredicate(state);
        Iterable<Booking> foundBookings = bookingDao.findAll(byBookerId.and(byAnyState), Sort.by(Sort.Direction.DESC, "start"));
        List<BookingDto> result = BookingMapper.bookingListToDto(foundBookings);
        return result;
    }

    @Override
    public List<BookingDto> findBookingsByOwnerId(long ownerId, BookingState state) {
        User booker = userDao.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором = " + ownerId + " не найден."));

        BooleanExpression byOwnerId = QBooking.booking.item.owner.id.eq(booker.getId());
        BooleanExpression byAnyState = createStatePredicate(state);
        Iterable<Booking> foundBookings = bookingDao.findAll(byOwnerId.and(byAnyState), Sort.by(Sort.Direction.DESC, "start"));
        List<BookingDto> result = BookingMapper.bookingListToDto(foundBookings);
        return result;
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
