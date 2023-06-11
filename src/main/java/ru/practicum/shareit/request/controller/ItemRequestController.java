package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public GetItemRequestDto createItemRequest(@Valid @RequestBody AddItemRequestDto addItemRequestDto,
                                               @RequestHeader("X-Sharer-User-Id") @Min(1) long userId) {
        log.info("Получен запрос POST /requests");
        return itemRequestService.createItemRequest(addItemRequestDto, userId);
    }

    @GetMapping
    public List<GetItemRequestDto> getItemRequestByRequester(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId) {
        log.info("Получен запрос GET /requests");
        return itemRequestService.findAllByRequesterId(userId);
    }

    @GetMapping("/all")
    public List<GetItemRequestDto> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId,
                                                     @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                     @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        log.info("Получен запрос GET /requests/all?from={from}&size={size}");
        return itemRequestService.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public GetItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId,
                                            @PathVariable @Min(1) long requestId) {
        log.info("Получен запрос GET /requests/{requestId}");
        return itemRequestService.getById(requestId, userId);
    }

}
