package ru.practicum.shareit.request.dao;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.*;

public class ItemRequestDaoImplInMemory implements ItemRequestDao{
    private Long id = 1L;
    private Map<Long, ItemRequest> itemRequests = new HashMap<>();

    @Override
    public ItemRequest createItemRequest(ItemRequest itemRequest) {
        itemRequest.setId(getId());
        return itemRequests.put(itemRequest.getId(), itemRequest);
    }

    @Override
    public Optional<ItemRequest> getById(Long id) {
        return Optional.ofNullable(itemRequests.get(id));
    }

    @Override
    public ItemRequest updateItemRequest(ItemRequest itemRequest) {
        return itemRequests.put(itemRequest.getId(), itemRequest);
    }

    @Override
    public List<ItemRequest> findAllItemRequests() {
        return new ArrayList<>(itemRequests.values());
    }

    @Override
    public void deleteItemRequest(Long id) {
        itemRequests.remove(id);
    }

    private long getId() {
        return id++;
    }
}
