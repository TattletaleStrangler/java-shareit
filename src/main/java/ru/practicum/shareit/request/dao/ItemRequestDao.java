package ru.practicum.shareit.request.dao;

import ru.practicum.shareit.request.model.ItemRequest;
import java.util.List;
import java.util.Optional;

public interface ItemRequestDao {
    ItemRequest createItemRequest(ItemRequest itemRequest);

    Optional<ItemRequest> getById(Long id);

    ItemRequest updateItemRequest(ItemRequest itemRequest);

    List<ItemRequest> findAllItemRequests();

    void deleteItemRequest(Long id);
}
