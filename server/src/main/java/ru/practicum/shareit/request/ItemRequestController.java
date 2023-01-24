package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequest add(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                           @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addNewRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                               @RequestParam(required = false) Integer from,
                                               @RequestParam(required = false) Integer size) {
        return itemRequestService.getAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                     @PathVariable long requestId) {
        return itemRequestService.getRequest(userId, requestId);
    }
}