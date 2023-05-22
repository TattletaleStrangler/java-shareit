package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@Entity
@Table(name = "items", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "available")
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "request_id")
    private ItemRequest request;

}
