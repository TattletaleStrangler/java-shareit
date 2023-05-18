package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao extends JpaRepository<Item, Long> {

    //    List<Item> findAllItemsByUserId(long userId);
//    @Query("select it from Item as it where it.owner.id = ?1")
    List<Item> findAllByOwnerId(long id);

//    @Query(value = "select * from items as it " +
//            "left join users as us on it.user_id = us.id " +
//            "left join requests as req on it.request_id = req.id " +
//            "where lower(it.name) like '%?1%' or lower(it.description) like '%?2%'", nativeQuery = true)
    @Query("SELECT it from Item as it where LOWER(it.name) LIKE %:text% or LOWER(it.description) LIKE %:text%") //JOIN it.owner JOIN it.request
    List<Item> searchByText(@Param("text")String text);
//    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String text);
}
