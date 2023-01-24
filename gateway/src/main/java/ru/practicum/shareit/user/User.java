package ru.practicum.shareit.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
   private long id;
    private String name;
    private String email;
}

