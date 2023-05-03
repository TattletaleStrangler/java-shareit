package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {

    Item createItem(Item item);

    Optional<Item> getById(Long id);

    Item updateItem(Item item);

    List<Item> findAllItems();

    void deleteItem(Long id);

    List<Item> getItemsByUserId(Long userId);

}
