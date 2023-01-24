package ru.practicum.shareit.item;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
    private Item item;

    public Comment(String text) {
        this.text = text;
    }
}
