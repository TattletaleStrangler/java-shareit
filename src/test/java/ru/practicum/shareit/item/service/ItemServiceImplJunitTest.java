package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.CommentDao;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class ItemServiceImplJunitTest {

    private static final long userId = 1L;
    private static final long ownerId = 2L;
    private static final long userId3 = 3L;
    private static final long itemId = 1L;
    private static final long bookingId = 1L;
    private static final long requestId = 1L;

    private ItemDto itemDto;

    @Mock
    ItemDao itemDao;
    @Mock
    UserDao userDao;
    @Mock
    BookingDao bookingDao;
    @Mock
    CommentDao commentDao;
    @Mock
    ItemRequestDao itemRequestDao;

    ItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemDao, userDao, bookingDao, commentDao, itemRequestDao);
        itemDto = ItemDto.builder().name("item name").description("description").available(true).requestId(requestId).build();
    }

    @Test
    void createItem_whenUserNotFound_thenThrowUserNotFoundException() {
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemService.createItem(itemDto, userId)
        );

        assertEquals("Пользователь с идентификатором " + userId + " не найден.", exception.getMessage());
    }

    @Test
    void createItem_whenRequestNotFound_thenThrowItemRequestNotFoundException() {
        User user = User.builder().build();
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        when(itemRequestDao.findById(requestId)).thenReturn(Optional.empty());

        final ItemRequestNotFoundException exception = assertThrows(
                ItemRequestNotFoundException.class,
                () -> itemService.createItem(itemDto, userId)
        );

        assertEquals("Запрос с идентификатором " + requestId + " не найден.", exception.getMessage());
    }

    @Test
    void getById_whenUserNotFound_thenThrowUserNotFoundException() {
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemService.getById(itemId, userId)
        );

        assertEquals("Пользователь с идентификатором " + userId + " не найден.", exception.getMessage());
    }

    @Test
    void getById_whenItemNotFound_thenThrowItemNotFoundException() {
        User user = User.builder().build();
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        when(itemDao.findById(itemId)).thenReturn(Optional.empty());

        final ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.getById(itemId, userId)
        );

        assertEquals("Предмет с идентификатором " + itemId + " не найден.", exception.getMessage());
    }


    @Test
    void getById_whenUserIsNotOwner() {
        User user = User.builder().build();
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        User owner = User.builder().id(ownerId).build();
        Item item = ItemMapper.dtoToItem(itemDto, owner, null);
        item.setId(itemId);
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));

        when(commentDao.findByItemId(itemId)).thenReturn(List.of());

        ItemDtoWithBooking itemDtoWithBooking = itemService.getById(itemId, userId);
        ItemDtoWithBooking expectedItemDtoWithBooking = ItemDtoWithBooking.builder()
                .id(itemId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .comments(List.of())
                .build();

        assertThat(itemDtoWithBooking, equalTo(expectedItemDtoWithBooking));
    }

    @Test
    void updateItem_whenUserNotFound_thenThrowUserNotFoundException() {
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemService.updateItem(itemDto, itemId, userId)
        );

        assertEquals("Пользователь с идентификатором " + userId + " не найден.", exception.getMessage());
    }

    @Test
    void updateItem_whenItemNotFound_thenThrowItemNotFoundException() {
        User user = User.builder().build();
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        when(itemDao.findById(itemId)).thenReturn(Optional.empty());

        final ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.updateItem(itemDto, itemId, userId)
        );

        assertEquals("Предмет с идентификатором " + itemId + " не найден.", exception.getMessage());
    }

    @Test
    void updateItem_whenUserIsNotOwner() {
        User user = User.builder().id(userId).build();
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        User owner = User.builder().id(ownerId).build();
        Item item = ItemMapper.dtoToItem(itemDto, owner, null);
        item.setId(itemId);
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));

        final NotEnoughRightsException exception = assertThrows(
                NotEnoughRightsException.class,
                () -> itemService.updateItem(itemDto, itemId, userId)
        );

        assertEquals("Пользователь не может редактировать чужие предметы.", exception.getMessage());
    }

    @Test
    void updateItem_whenNewNameIsNull() {
        User user = User.builder().id(userId).build();
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        User owner = User.builder().id(userId).build();
        Item item = ItemMapper.dtoToItem(itemDto, owner, null);
        item.setId(itemId);
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));

        when(itemDao.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto newItemDto = ItemDto.builder().name(null).build();
        ItemDto savedItemDto = itemService.updateItem(newItemDto, itemId, userId);
        assertThat(savedItemDto.getName(), equalTo(itemDto.getName()));
    }

    @Test
    void updateItem_whenNewNameIsBlank() {
        User user = User.builder().id(userId).build();
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        User owner = User.builder().id(userId).build();
        Item item = ItemMapper.dtoToItem(itemDto, owner, null);
        item.setId(itemId);
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));

        when(itemDao.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto newItemDto = ItemDto.builder().name("").build();
        ItemDto savedItemDto = itemService.updateItem(newItemDto, itemId, userId);
        assertThat(savedItemDto.getName(), equalTo(itemDto.getName()));
    }

    @Test
    void updateItem_whenNewDescriptionIsNull() {
        User user = User.builder().id(userId).build();
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        User owner = User.builder().id(userId).build();
        Item item = ItemMapper.dtoToItem(itemDto, owner, null);
        item.setId(itemId);
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));

        when(itemDao.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto newItemDto = ItemDto.builder().description(null).build();
        ItemDto savedItemDto = itemService.updateItem(newItemDto, itemId, userId);
        assertThat(savedItemDto.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void updateItem_whenNewDescriptionIsBlank() {
        User user = User.builder().id(userId).build();
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        User owner = User.builder().id(userId).build();
        Item item = ItemMapper.dtoToItem(itemDto, owner, null);
        item.setId(itemId);
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));

        when(itemDao.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto newItemDto = ItemDto.builder().description("").build();
        ItemDto savedItemDto = itemService.updateItem(newItemDto, itemId, userId);
        assertThat(savedItemDto.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void updateItem_whenNewDescriptionIsNotBlank() {
        User user = User.builder().id(userId).build();
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        User owner = User.builder().id(userId).build();
        Item item = ItemMapper.dtoToItem(itemDto, owner, null);
        item.setId(itemId);
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));

        when(itemDao.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto newItemDto = ItemDto.builder().description("new description").build();
        ItemDto savedItemDto = itemService.updateItem(newItemDto, itemId, userId);
        assertThat(savedItemDto.getDescription(), equalTo(newItemDto.getDescription()));
    }

    @Test
    void updateItem_whenNewAvailableIsNotNull() {
        User user = User.builder().id(userId).build();
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        User owner = User.builder().id(userId).build();
        Item item = ItemMapper.dtoToItem(itemDto, owner, null);
        item.setId(itemId);
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));

        when(itemDao.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto newItemDto = ItemDto.builder().available(true).build();
        ItemDto savedItemDto = itemService.updateItem(newItemDto, itemId, userId);
        assertThat(savedItemDto.getAvailable(), equalTo(newItemDto.getAvailable()));
    }

    @Test
    void findAllItemsByOwnerId_whenUserHasNoItems() {
        when(itemDao.findAllByOwnerIdOrderById(anyLong(), any()))
                .thenReturn(List.of());

        List<ItemDtoWithBooking> itemDtoList = itemService.findAllItemsByOwnerId(userId, 0, 10);
        assertTrue(itemDtoList.isEmpty());
    }

    @Test
    void findAllItemsByOwnerId_whenFromGreaterThanSize() {
        when(itemDao.findAllByOwnerIdOrderById(anyLong(), any()))
                .thenReturn(List.of());

        int size = 1;
        int from = size + 1;
        List<ItemDtoWithBooking> itemDtoList = itemService.findAllItemsByOwnerId(userId, from, size);
        assertTrue(itemDtoList.isEmpty());
    }

    @Test
    void searchByText_whenTextIsEmpty() {
        List<ItemDto> itemDtoList = itemService.searchByText("", 0, 10);

        assertThat(itemDtoList, equalTo(List.of()));
    }

    @Test
    void searchByText_whenFromGreaterThanSize() {
        int size = 1;
        int from = size + 1;
        String anyText = "any text";

        PageRequest page = PageRequest.of(from / size, size);
        when(itemDao.searchByText(anyText, page)).thenReturn(List.of());

        List<ItemDto> itemDtoList = itemService.searchByText(anyText, from, size);
        assertThat(itemDtoList, equalTo(List.of()));
        Mockito.verify(itemDao, Mockito.times(1))
                .searchByText(anyText, page);
        Mockito.verifyNoMoreInteractions(itemDao);
    }

    @Test
    void addComment_whenUserNotFound_thenThrowUserNotFoundException() {
        when(itemDao.findById(itemId)).thenReturn(Optional.of(Item.builder().build()));

        when(userDao.findById(userId)).thenReturn(Optional.empty());

        CommentDto commentDto = new CommentDto("comment text");
        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemService.addComment(commentDto, userId, itemId)
        );

        assertEquals("Пользователь с идентификатором " + userId + " не найден.", exception.getMessage());
    }

    @Test
    void addComment_whenItemNotFound_thenThrowItemNotFoundException() {
        when(itemDao.findById(itemId)).thenReturn(Optional.empty());

        CommentDto commentDto = new CommentDto("comment text");
        final ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.addComment(commentDto, userId, itemId)
        );

        assertEquals("Предмет с идентификатором " + itemId + " не найден.", exception.getMessage());
    }

    @Test
    void addComment_whenUserIsNotBooker_thenValidationException() {
        when(itemDao.findById(itemId)).thenReturn(Optional.of(Item.builder().build()));

        when(userDao.findById(userId)).thenReturn(Optional.of(User.builder().build()));

        when(bookingDao.existsByItemIdAndBookerIdAndStatusAndEndLessThan(anyLong(), anyLong(), any(BookingStatus.class),
                any(LocalDateTime.class)))
                .thenReturn(false);

        CommentDto commentDto = new CommentDto("comment text");
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.addComment(commentDto, userId, itemId)
        );

        assertEquals("Добавлять комментарий к вещи может только пользователь, арендовавший её ранее.", exception.getMessage());
    }
}