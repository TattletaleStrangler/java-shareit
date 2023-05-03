package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Mapper;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemDao itemDao;

    public Item createItem(Item item) {
        return itemDao.createItem(item);
    }

    public ItemDto getById(Long itemId) {
        Item item = itemDao.getById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет с идентификатором " + itemId + " не найден."));

        return Mapper.itemToDto(item);
    }

    public ItemDto updateItem(Item item) {
        return Mapper.itemToDto(itemDao.updateItem(item));
    }

    public List<ItemDto> findAllItems() {
        return itemDao.findAllItems().stream()
                .map(Mapper::itemToDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(long id) {
        itemDao.deleteItem(id);
    }

}
