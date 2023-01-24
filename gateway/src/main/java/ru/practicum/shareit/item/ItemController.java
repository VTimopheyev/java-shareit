package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Optional;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                      @RequestBody @Valid ItemDto itemDTO) {
        checkUserIdPresent(userId);
        log.info("Creating item {}, userId={}", itemDTO, userId);
        return itemClient.addNewItem(userId.get(), itemDTO);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId, @PathVariable long itemId,
                                         @RequestBody ItemDto itemDTO) {
        checkUserIdPresent(userId);
        log.info("Updating item {} userId = {}", itemDTO, userId);
        return itemClient.updateItem(userId.get(), itemId, itemDTO);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                           @PathVariable long itemId) {
        checkUserIdPresent(userId);
        itemClient.deleteItem(userId.get(), itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                              @RequestParam String text,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "5") Integer size) {
        checkUserIdPresent(userId);
        return itemClient.searchItems(userId.get(), text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                             @PathVariable long itemId,
                                             @RequestBody Comment comment) {
        checkUserIdPresent(userId);
        return itemClient.addNewComment(userId.get(), itemId, comment);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                          @PathVariable long itemId) {
        checkUserIdPresent(userId);
        return itemClient.getItem(userId.get(), itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "5") Integer size) {
        return itemClient.getAllItems(userId.get(), from, size);
    }

    private void checkUserIdPresent(Optional<Long> userId) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }
    }
}
