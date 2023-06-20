package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    GetItemRequestDto createItemRequest(AddItemRequestDto itemRequestDto, long userId);

    List<GetItemRequestDto> findAllByRequesterId(long userId);

    List<GetItemRequestDto> findAll(long userId, int from, int size);

    GetItemRequestDto getById(long itemId, long userId);

}
