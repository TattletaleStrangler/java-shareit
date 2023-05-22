package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDtoForItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
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
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;
    private final BookingDao bookingDao;
    private final CommentDao commentDao;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        ItemValidator.itemDtoValidation(itemDto);
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

        itemDto.setId(itemId);
        Item newItem = ItemMapper.dtoToItem(itemDto, user);
        updateItem(newItem, oldItem);
        return ItemMapper.itemToDto(itemDao.save(newItem));
    }

    @Override
    public List<ItemDtoWithBooking> findAllItemsByOwnerId(long userId) {
        List<Item> items = itemDao.findAllByOwnerIdOrderById(userId);
        List<ItemDtoWithBooking> itemDtoWithBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Item item : items) {
            BookingDtoForItemDto bookingDtoLast =
                    BookingMapper.bookingToBookingDtoForItemDto(bookingDao.findFirstByItemIdAndStatusAndStartLessThanEqualOrderByStartDesc(item.getId(), BookingStatus.APPROVED, now));
            BookingDtoForItemDto bookingDtoNext =
                    BookingMapper.bookingToBookingDtoForItemDto(bookingDao.findFirstByItemIdAndStatusAndStartGreaterThanOrderByStart(item.getId(), BookingStatus.APPROVED, now));
            ItemDtoWithBooking itemDtoWithBooking = ItemMapper.itemToDtoWithDate(item, bookingDtoLast, bookingDtoNext);

            List<Comment> comments = commentDao.findByItemId(item.getId());
            List<CommentDtoResponse> commentsDto = ItemMapper.commentsToDtoResponse(comments);
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
        if (text.length() == 0) {
            return new ArrayList<>();
        }

        return itemDao.searchByText(text.toLowerCase()).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDtoResponse addComment(CommentDto commentDto, long userId, long itemId) {
        ItemValidator.commentValidation(commentDto);
        Item item = itemDao.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет с идентификатором " + itemId + " не найден."));
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором " + userId + " не найден."));

        if (bookingDao.findFirstByItemIdAndBookerIdAndStatusAndEndLessThan(itemId, userId, BookingStatus.APPROVED,
                LocalDateTime.now()) == null) {
            throw new ValidationException("Добавлять комментарий к вещи может только пользователь, арендовавший её ранее.");
        }

        Comment comment = ItemMapper.dtoToComment(commentDto, item, user, LocalDateTime.now());
        Comment savedComment = commentDao.save(comment);
        CommentDtoResponse savedCommetDto = ItemMapper.commentDtoResponse(savedComment);
        return savedCommetDto;
    }

    private void updateItem(Item newItem, Item oldItem) {
        if (newItem.getName() == null) {
            newItem.setName(oldItem.getName());
        } else {
            ItemValidator.nameValidation(newItem.getName());
        }

        if (newItem.getDescription() == null) {
            newItem.setDescription(oldItem.getDescription());
        } else {
            ItemValidator.descriptionValidation(newItem.getDescription());
        }

        if (newItem.getOwner() == null) {
            newItem.setOwner(oldItem.getOwner());
        }

        if (newItem.getAvailable() == null) {
            newItem.setAvailable(oldItem.getAvailable());
        }
    }
}
