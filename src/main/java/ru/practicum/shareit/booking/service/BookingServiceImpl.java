package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSmall;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NotEnoughRightsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

/**
 * TODO Sprint add-bookings.
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final BookingDao bookingDao;
    private final UserDao userDao;
    private final ItemDao itemDao;

    @Override
    public BookingDto createBooking(BookingDtoSmall bookingDtoSmall, long userId) {
        User booker = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором = " + userId + " не найден."));
        Item item = itemDao.findById(bookingDtoSmall.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Предмет с идентификатором " + bookingDtoSmall.getItemId() + " не найден."));
        Booking booking = BookingMapper.dtoSmallToBooking(bookingDtoSmall, booker, item, BookingStatus.WAITING);
        Booking savedBooking = bookingDao.save(booking);
        BookingDto bookingDto = BookingMapper.bookingToDto(savedBooking);
        return bookingDto;
    }

    @Override
    public BookingDto updateBooking(BookingDto bookingDto, long itemId, long userId) {
        return null;
    }

    @Override
    public BookingDto getById(long bookingId, long userId) {
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с идентификатором " + bookingId + " не найдено."));
        User ownerOrBooker = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором = " + userId + " не найден."));

        if (!Objects.equals(ownerOrBooker.getId(), booking.getBooker().getId()) ||
                !Objects.equals(ownerOrBooker.getId(), booking.getItem().getOwner().getId())) {
            throw new NotEnoughRightsException("Получать информацию о бронировании может только его автор или владелец вещи.");
        }

        BookingDto bookingDto = BookingMapper.bookingToDto(booking);
        return bookingDto;
    }

    @Override
    public BookingDto approve(long bookingId, long userId, boolean approved) {
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с идентификатором " + bookingId + " не найдено."));
        User owner = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором = " + userId + " не найден."));

        if (!Objects.equals(booking.getItem().getOwner().getId(), owner.getId())) {
            throw new NotEnoughRightsException("Подтвердить или отклонить бронирование может только владелец вещи.");
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
    public List<BookingDto> findBookingsByBookerId(long userId, BookingState state) {
        User booker = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором = " + userId + " не найден."));

        bookingDao.findBookingsByBookerIdAndState()

        return null;
    }

    @Override
    public List<BookingDto> findBookingsByOwnerId(long userId, BookingState state) {
        return null;
    }
}
