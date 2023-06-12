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

    private static final long USER_ID = 1L;
    private static final long REQUEST_ID = 1L;

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
        when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.createItemRequest(addItemRequestDto, USER_ID)
        );

        assertEquals("Пользователь с идентификатором " + USER_ID + " не найден.", exception.getMessage());
        Mockito.verify(userDao, Mockito.times(1))
                .findById(USER_ID);
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void findAllByRequesterId_whenUserNotExists_thenThrowException() {
        when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.findAllByRequesterId(USER_ID)
        );

        assertEquals("Пользователь с идентификатором " + USER_ID + " не найден.", exception.getMessage());
        Mockito.verify(userDao, Mockito.times(1))
                .findById(USER_ID);
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void findAll_whenUserNotExists_thenThrowException() {
        when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        int from = 0;
        int size = 10;
        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.findAll(USER_ID, from, size)
        );

        assertEquals("Пользователь с идентификатором " + USER_ID + " не найден.", exception.getMessage());
        Mockito.verify(userDao, Mockito.times(1))
                .findById(USER_ID);
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void getById_whenUserNotExists_thenThrowException() {
        when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.getById(REQUEST_ID, USER_ID)
        );

        assertEquals("Пользователь с идентификатором " + USER_ID + " не найден.", exception.getMessage());
        Mockito.verify(userDao, Mockito.times(1))
                .findById(USER_ID);
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void getById_whenItemRequestNotExists_thenThrowException() {
        User user = User.builder().build();
        when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));

        when(itemRequestDao.findById(REQUEST_ID)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.getById(REQUEST_ID, USER_ID)
        );

        assertEquals("Запрос с идентификатором " + REQUEST_ID + " не найден.", exception.getMessage());
        Mockito.verify(userDao, Mockito.times(1))
                .findById(USER_ID);
        Mockito.verify(itemRequestDao, Mockito.times(1))
                .findById(REQUEST_ID);
        Mockito.verifyNoMoreInteractions(userDao);
        Mockito.verifyNoMoreInteractions(itemRequestDao);
    }
}