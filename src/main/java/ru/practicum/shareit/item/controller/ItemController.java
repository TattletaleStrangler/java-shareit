package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
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
    public List<ItemDtoWithBooking> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                    @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        log.info("Получен запрос GET /items");
        return itemService.findAllItemsByOwnerId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam(defaultValue = "") String text,
                                      @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                      @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        log.info("Получен запрос GET /items/search?text={}", text);
        return itemService.searchByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse addComment(@Valid @RequestBody CommentDto commentDto, @PathVariable long itemId,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос к эндпоинту POST /items/{itemId}/comment");
        return itemService.addComment(commentDto, userId, itemId);
    }

}
