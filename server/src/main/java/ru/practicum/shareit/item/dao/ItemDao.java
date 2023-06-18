package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemDao extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    List<Item> findAllByOwnerIdOrderById(long id, PageRequest page);

    @Query("SELECT it FROM Item AS it WHERE LOWER(it.name) LIKE %:text% OR LOWER(it.description) LIKE %:text% AND it.available = true")
    List<Item> searchByText(@Param("text") String text, PageRequest page);

    @Query("SELECT it FROM Item AS it WHERE it.request IN :itemRequests ORDER BY it.request.id, it.id")
    List<Item> findAllByItemRequests(List<ItemRequest> itemRequests);
}
