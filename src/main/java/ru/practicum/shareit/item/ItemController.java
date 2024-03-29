package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final BookingService bookingService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                       @RequestBody ItemDto itemDTO) {
        return itemService.addNewItem(userId, itemDTO);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId, @PathVariable long itemId,
                          @RequestBody ItemDto itemDTO) {
        return itemService.updateItem(userId, itemId, itemDTO);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                     @RequestParam String text,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "5") Integer size) {
        return itemService.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public Comment addComment(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                              @PathVariable long itemId,
                              @RequestBody Comment comment) {
        bookingService.checkUserBookedItem(itemId, userId);
        return itemService.addNewComment(userId, itemId, comment);
    }

    @GetMapping("/{itemId}")
    public Object getItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId, @PathVariable long itemId) {
        return bookingService.getItem(userId, itemId);
    }

    @GetMapping
    public List<OwnerItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "5") Integer size) {
        return bookingService.getAllItems(userId, from, size);
    }
}
