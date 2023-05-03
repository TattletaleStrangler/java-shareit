package ru.practicum.shareit.request.service;

import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemRequestService {

    private ItemRequestDao itemRequestDao;

    public ItemRequest createItemRequest(ItemRequest itemRequest) {
        return itemRequestDao.createItemRequest(itemRequest);
    }

    public ItemRequest getById(Long id) {
        return itemRequestDao.getById(id)
                .orElseThrow(() -> new ItemRequestNotFoundException("Запрос с идентификатором " + id + " не найден."));
    }

    public ItemRequest updateItemRequest(ItemRequest itemRequest) {
        return itemRequestDao.updateItemRequest(itemRequest);
    }

    public List<ItemRequest> findAllItemRequests() {
        return itemRequestDao.findAllItemRequests();
    }

    public void deleteItemRequest(Long id) {
        itemRequestDao.deleteItemRequest(id);
    }
}
