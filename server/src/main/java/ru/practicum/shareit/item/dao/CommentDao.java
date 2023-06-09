package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentDao extends JpaRepository<Comment, Long> {

    List<Comment> findByItemId(long itemId);

    List<Comment> findByItemIn(List<Item> items, Sort sort);

}
