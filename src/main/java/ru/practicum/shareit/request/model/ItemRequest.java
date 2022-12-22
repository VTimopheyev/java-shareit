package ru.practicum.shareit.request.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
@NoArgsConstructor
@EqualsAndHashCode
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String description;
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
