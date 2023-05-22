package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDtoForItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto itemToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemDtoWithBooking itemToDtoWithDate(Item item, BookingDtoForItemDto lastBooking, BookingDtoForItemDto nextBooking) {
        return ItemDtoWithBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }

    public static Item dtoToItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }

    public static Comment dtoToComment(CommentDto commentDto, Item item, User user, LocalDateTime created) {
        return Comment.builder()
                .text(commentDto.getText())
                .author(user)
                .item(item)
                .created(created)
                .build();
    }

    public static CommentDtoResponse CommentDtoResponse(Comment comment) {
        return CommentDtoResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentDtoResponse> commentsToDtoResponse(List<Comment> comments) {
        return comments.stream()
                .map(ItemMapper::CommentDtoResponse)
                .collect(Collectors.toList());
    }

}
