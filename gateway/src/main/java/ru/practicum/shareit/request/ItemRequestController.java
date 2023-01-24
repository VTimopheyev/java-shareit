package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserValidationException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Optional;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                      @RequestBody ItemRequestDto itemRequestDto) {
        checkUserIdPresent(userId);
        return itemRequestClient.addNewRequest(userId.get(), itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        checkUserIdPresent(userId);
        return itemRequestClient.getUserRequests(userId.get());
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        checkUserIdPresent(userId);
        return itemRequestClient.getAllRequests(from, size, userId.get());
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                             @PathVariable long requestId) {
        checkUserIdPresent(userId);
        return itemRequestClient.getRequest(userId.get(), requestId);
    }

    private void checkUserIdPresent(Optional<Long> userId) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }
    }
}
