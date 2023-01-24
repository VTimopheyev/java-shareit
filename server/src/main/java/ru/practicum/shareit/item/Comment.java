package ru.practicum.shareit.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
@NoArgsConstructor
@EqualsAndHashCode
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    public Comment(String text, String authorName, LocalDateTime created, Item item) {
        this.text = text;
        this.authorName = authorName;
        this.created = created;
        this.item = item;
    }

    public Comment(String text) {
        this.text = text;
    }
}
