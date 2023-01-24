package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody UserDto userDto) {
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable long id, @RequestBody UserDto userDto) {
        return userClient.update(id, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        return userClient.deleteUser(userId);
    }
}
