package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NotEnoughRightsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        User user = userDao.getById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором " + userId + " не найден."));
        Item item = ItemMapper.DtoToItem(itemDto, user);
        Item savedItem = itemDao.createItem(item);
        ItemDto savedItemDto = ItemMapper.itemToDto(savedItem);
        return savedItemDto;
    }

    @Override
    public ItemDto getById(long itemId) {
        Item item = itemDao.getById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет с идентификатором " + itemId + " не найден."));

        return ItemMapper.itemToDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        User user = userDao.getById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором " + userId + " не найден."));
        Item oldItem = itemDao.getById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет с идентификатором " + itemId + " не найден."));

        if (!user.equals(oldItem.getOwner())) {
            throw new NotEnoughRightsException("Пользователь не может редактировать чужие предметы.");
        }

        itemDto.setId(itemId);
        Item newItem = ItemMapper.DtoToItem(itemDto, user);
        updateItem(newItem, oldItem);
        return ItemMapper.itemToDto(itemDao.updateItem(newItem));
    }

    @Override
    public List<ItemDto> findAllItemsByUserId(long userId) {
        return itemDao.findAllItemsByUserId(userId).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    public void deleteItem(long id) {
        itemDao.deleteItem(id);
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        if (text.length() == 0) {
            return new ArrayList<>();
        }

        return itemDao.searchByText(text).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    private void updateItem(Item newItem, Item oldItem) {
        if (newItem.getName() == null) {
            newItem.setName(oldItem.getName());
        }
        if (newItem.getDescription() == null) {
            newItem.setDescription(oldItem.getDescription());
        }
        if (newItem.getOwner() == null) {
            newItem.setOwner(oldItem.getOwner());
        }
        if (newItem.getAvailable() == null) {
            newItem.setAvailable(oldItem.getAvailable());
        }
    }
}
