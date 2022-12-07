package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String description;
    private boolean available;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    @Nullable
    private ItemRequest request;

    public Item(String name, String description, boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
