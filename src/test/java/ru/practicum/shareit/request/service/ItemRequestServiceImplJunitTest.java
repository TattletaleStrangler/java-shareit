package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplJunitTest {

    private static final long userId = 1L;
    private static final long ownerId = 2L;
    private static final long userId3 = 3L;
    private static final long itemId = 1L;
    private static final long bookingId = 1L;
    private static final long requestId = 1L;

    private AddItemRequestDto addItemRequestDto;

    @Mock
    ItemRequestDao itemRequestDao;
    @Mock
    UserDao userDao;
    @Mock
    ItemDao itemDao;

    ItemRequestService itemRequestService;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestDao, userDao, itemDao);
        addItemRequestDto = new AddItemRequestDto("description");
    }

    @Test
    void createItemRequest_whenUserNotExists_thenThrowException() {
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.createItemRequest(addItemRequestDto, userId)
        );

        assertEquals("Пользователь с идентификатором " + userId + " не найден.", exception.getMessage());
        Mockito.verify(userDao, Mockito.times(1))
                .findById(userId);
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void findAllByRequesterId_whenUserNotExists_thenThrowException() {
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.findAllByRequesterId(userId)
        );

        assertEquals("Пользователь с идентификатором " + userId + " не найден.", exception.getMessage());
        Mockito.verify(userDao, Mockito.times(1))
                .findById(userId);
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void findAll_whenUserNotExists_thenThrowException() {
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        int from = 0;
        int size = 10;
        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.findAll(userId, from, size)
        );

        assertEquals("Пользователь с идентификатором " + userId + " не найден.", exception.getMessage());
        Mockito.verify(userDao, Mockito.times(1))
                .findById(userId);
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void getById_whenUserNotExists_thenThrowException() {
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.getById(requestId, userId)
        );

        assertEquals("Пользователь с идентификатором " + userId + " не найден.", exception.getMessage());
        Mockito.verify(userDao, Mockito.times(1))
                .findById(userId);
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void getById_whenItemRequestNotExists_thenThrowException() {
        User user = User.builder().build();
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        when(itemRequestDao.findById(requestId)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.getById(requestId, userId)
        );

        assertEquals("Запрос с идентификатором " + requestId + " не найден.", exception.getMessage());
        Mockito.verify(userDao, Mockito.times(1))
                .findById(userId);
        Mockito.verify(itemRequestDao, Mockito.times(1))
                .findById(requestId);
        Mockito.verifyNoMoreInteractions(userDao);
        Mockito.verifyNoMoreInteractions(itemRequestDao);
    }
}