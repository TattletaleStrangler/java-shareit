package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequest dtoToItemRequest(AddItemRequestDto itemRequestDto, User requester) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .requester(requester)
                .build();
    }

    public GetItemRequestDto itemRequestToGetItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        return GetItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(ItemMapper.itemsToDto(items))
                .build();
    }

    public List<GetItemRequestDto> itemRequestsToGetItemRequestDto(List<ItemRequest> itemRequests, Map<Long, List<Item>> items) {
        return itemRequests
                .stream()
                .map(ir -> itemRequestToGetItemRequestDto(ir,
                        items.getOrDefault(ir.getId(), List.of())))
                .collect(Collectors.toList());
    }

}
