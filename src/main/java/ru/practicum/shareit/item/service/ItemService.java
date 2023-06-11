package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDtoWithBooking getById(long itemId, long userId);

    List<ItemDtoWithBooking> findAllItemsByOwnerId(long userId, int from, int size);

    List<ItemDto> searchByText(String text, int from, int size);

    CommentDtoResponse addComment(CommentDto commentDto, long userId, long itemId);

    void deleteItem(long itemId);
}
