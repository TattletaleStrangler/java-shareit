package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getById(long itemId);

    List<ItemDto> findAllItemsByUserId(long userId);

    List<ItemDto> searchByText(String text);
}
