package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос POST /items");
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос PATCH /items/{}", itemId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /items/{}", itemId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /items");
        return itemService.findAllItemsByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam(defaultValue = "") String text) {
        log.info("Получен запрос GET /items/search?text={}", text);
        return itemService.searchByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse addComment(@Valid @RequestBody CommentDto commentDto, @PathVariable long itemId,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос к эндпоинту POST /items/{itemId}/comment");
        return itemService.addComment(commentDto, userId, itemId);
    }

}
