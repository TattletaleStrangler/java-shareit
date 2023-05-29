package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    List<Item> findAllByOwnerIdOrderById(long id);

    @Query("SELECT it FROM Item AS it WHERE LOWER(it.name) LIKE %:text% OR LOWER(it.description) LIKE %:text% AND it.available = true")
    List<Item> searchByText(@Param("text") String text);

}
