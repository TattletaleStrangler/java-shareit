package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDtoForItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NotEnoughRightsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentDao;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;
    private final BookingDao bookingDao;
    private final CommentDao commentDao;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором " + userId + " не найден."));
        Item item = ItemMapper.dtoToItem(itemDto, user);
        Item savedItem = itemDao.save(item);
        ItemDto savedItemDto = ItemMapper.itemToDto(savedItem);
        return savedItemDto;
    }

    @Override
    public ItemDtoWithBooking getById(long itemId, long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором " + userId + " не найден."));
        Item item = itemDao.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет с идентификатором " + itemId + " не найден."));

        ItemDtoWithBooking itemDtoWithBooking;
        if (item.getOwner().getId().equals(user.getId())) {
            LocalDateTime now = LocalDateTime.now();
            BookingDtoForItemDto bookingDtoLast =
                    BookingMapper.bookingToBookingDtoForItemDto(bookingDao.findFirstByItemIdAndStatusAndStartLessThanEqualOrderByStartDesc(item.getId(), BookingStatus.APPROVED, now));
            BookingDtoForItemDto bookingDtoNext =
                    BookingMapper.bookingToBookingDtoForItemDto(bookingDao.findFirstByItemIdAndStatusAndStartGreaterThanOrderByStart(item.getId(), BookingStatus.APPROVED, now));
            itemDtoWithBooking = ItemMapper.itemToDtoWithDate(item, bookingDtoLast, bookingDtoNext);
        } else {
            itemDtoWithBooking = ItemMapper.itemToDtoWithDate(item, null, null);
        }

        List<Comment> comments = commentDao.findByItemId(item.getId());
        List<CommentDtoResponse> commentsDto = ItemMapper.commentsToDtoResponse(comments);
        itemDtoWithBooking.setComments(commentsDto);
        return itemDtoWithBooking;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором " + userId + " не найден."));
        Item oldItem = itemDao.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет с идентификатором " + itemId + " не найден."));

        if (!user.equals(oldItem.getOwner())) {
            throw new NotEnoughRightsException("Пользователь не может редактировать чужие предметы.");
        }

        updateItem(itemDto, oldItem);
        return ItemMapper.itemToDto(itemDao.save(oldItem));
    }

    @Override
    public List<ItemDtoWithBooking> findAllItemsByOwnerId(long userId) {
        List<Item> items = itemDao.findAllByOwnerIdOrderById(userId);
        List<ItemDtoWithBooking> itemDtoWithBookings = new ArrayList<>();

        if (items.isEmpty()) {
            return itemDtoWithBookings;
        }

        LocalDateTime now = LocalDateTime.now();

        Map<Item, List<Comment>> comments = commentDao.findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .collect(Collectors.groupingBy(Comment::getItem, toList()));

        Map<Long, Booking> lastBookings = bookingDao.findLastByItems(items, BookingStatus.APPROVED, now)
                .stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), identity(), (existing, replacement) -> existing));

        Map<Long, Booking> nextBookings = bookingDao.findNextByItems(items, BookingStatus.APPROVED, now)
                .stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), identity(), (existing, replacement) -> existing));

        for (Item item : items) {
            BookingDtoForItemDto bookingDtoLast = null;

            if (lastBookings != null && lastBookings.size() > 0) {
                bookingDtoLast = BookingMapper.bookingToBookingDtoForItemDto(lastBookings.get(item.getId()));
            }

            BookingDtoForItemDto bookingDtoNext = null;
            if (nextBookings != null && nextBookings.size() > 0) {
                bookingDtoNext = BookingMapper.bookingToBookingDtoForItemDto(nextBookings.get(item.getId()));
            }

            ItemDtoWithBooking itemDtoWithBooking = ItemMapper.itemToDtoWithDate(item, bookingDtoLast, bookingDtoNext);
            List<CommentDtoResponse> commentsDto = ItemMapper.commentsToDtoResponse(comments.get(item));
            itemDtoWithBooking.setComments(commentsDto);
            itemDtoWithBookings.add(itemDtoWithBooking);
        }

        return itemDtoWithBookings;
    }

    public void deleteItem(long id) {
        itemDao.deleteById(id);
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        if (text.isBlank()) {
            return List.of();
        }

        return itemDao.searchByText(text.toLowerCase()).stream()
                .map(ItemMapper::itemToDto)
                .collect(toList());
    }

    @Override
    public CommentDtoResponse addComment(CommentDto commentDto, long userId, long itemId) {
        Item item = itemDao.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет с идентификатором " + itemId + " не найден."));
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором " + userId + " не найден."));

        if (!bookingDao.existsByItemIdAndBookerIdAndStatusAndEndLessThan(itemId, userId, BookingStatus.APPROVED,
                LocalDateTime.now())) {
            throw new ValidationException("Добавлять комментарий к вещи может только пользователь, арендовавший её ранее.");
        }

        Comment comment = ItemMapper.dtoToComment(commentDto, item, user, LocalDateTime.now());
        Comment savedComment = commentDao.save(comment);
        CommentDtoResponse savedCommetDto = ItemMapper.commentDtoResponse(savedComment);
        return savedCommetDto;
    }

    private void updateItem(ItemDto newItem, Item oldItem) {
        String name = newItem.getName();
        if (name != null && !name.isBlank()) {
            oldItem.setName(name);
        }

        String description = newItem.getDescription();
        if (description != null && !description.isBlank()) {
            oldItem.setDescription(description);
        }

        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
    }
}
