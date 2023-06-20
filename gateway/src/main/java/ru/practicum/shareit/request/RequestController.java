package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody AddItemRequestDto addItemRequestDto,
                                                    @RequestHeader("X-Sharer-User-Id") @Min(1) long userId) {
        log.info("Получен запрос POST /requests");
        return requestClient.createItemRequest(addItemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestByRequester(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId) {
        log.info("Получен запрос GET /requests");
        return requestClient.findAllByRequesterId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId,
                                                    @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                    @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        log.info("Получен запрос GET /requests/all?from={from}&size={size}");
        return requestClient.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") @Min(1) long userId,
                                                 @PathVariable @Min(1) long requestId) {
        log.info("Получен запрос GET /requests/{requestId}");
        return requestClient.getById(requestId, userId);
    }

}
