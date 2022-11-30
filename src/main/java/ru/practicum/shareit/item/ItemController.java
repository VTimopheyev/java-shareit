package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId, @PathVariable long itemId) {
        return itemService.getItem(userId, itemId);
    }

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
                                     @RequestParam String text) {
        return itemService.searchItems(userId, text);
    }
}
