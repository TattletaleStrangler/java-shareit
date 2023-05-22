package ru.practicum.shareit.request.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
@Entity
@Table(name = "requests", schema = "public")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "requestor_id")
    private User requestor;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

}
