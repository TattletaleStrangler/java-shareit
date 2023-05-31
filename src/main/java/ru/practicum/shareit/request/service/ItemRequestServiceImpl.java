package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestDao itemRequestDao;
    private final UserDao userDao;
    private final ItemDao itemDao;

    @Override
    public GetItemRequestDto createItemRequest(AddItemRequestDto itemRequestDto, long userId) {
        User requester = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором " + userId + " не найден."));

        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(itemRequestDto, requester);
        ItemRequest savedItemRequest = itemRequestDao.save(itemRequest);
        return ItemRequestMapper.itemRequestToGetItemRequestDto(savedItemRequest, List.of());
    }

    @Override
    public List<GetItemRequestDto> findAllByRequesterId(long requesterId) {
        User requester = userDao.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором " + requesterId + " не найден."));

        List<ItemRequest> itemRequests = itemRequestDao.findAllByRequesterIdOrderByCreatedDesc(requesterId);

        Map<Long, List<Item>> items = itemDao.findAllByItemRequests(itemRequests)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId(), toList()));

        return ItemRequestMapper.itemRequestsToGetItemRequestDto(itemRequests, items);
    }

    @Override
    public List<GetItemRequestDto> findAll(long userId, int from, int size) {
        User requester = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором " + userId + " не найден."));

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> itemRequests = itemRequestDao.findAllByRequesterIdIsNotOrderByCreatedDesc(userId, page);

        Map<Long, List<Item>> items = itemDao.findAllByItemRequests(itemRequests)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId(), toList()));

        return ItemRequestMapper.itemRequestsToGetItemRequestDto(itemRequests, items);
    }

    @Override
    public GetItemRequestDto getById(long itemId, long userId) {
        User requester = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором " + userId + " не найден."));

        ItemRequest itemRequest = itemRequestDao.findById(itemId)
                .orElseThrow(() -> new UserNotFoundException("Запрос с идентификатором " + itemId + " не найден."));

        List<Item> items = itemDao.findAllByItemRequests(List.of(itemRequest));
        return ItemRequestMapper.itemRequestToGetItemRequestDto(itemRequest, items);
    }

}
