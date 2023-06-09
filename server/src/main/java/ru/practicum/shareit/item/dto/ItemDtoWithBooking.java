package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoForItemDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemDtoWithBooking {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingDtoForItemDto lastBooking;
    private BookingDtoForItemDto nextBooking;
    private List<CommentDtoResponse> comments;

}
