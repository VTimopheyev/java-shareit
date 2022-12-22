package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;
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
                           @RequestBody ItemRequestDto ItemRequestDto) {
        return itemRequestService.addNewRequest(userId, ItemRequestDto);
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
    public ItemRequestDto getAllRequests(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                         @PathVariable long requestId) {
        return itemRequestService.getRequest(userId, requestId);
    }
}


/*POST /requests — добавить новый запрос вещи. Основная часть запроса — текст запроса, где пользователь описывает, какая именно вещь ему нужна.
GET /requests — получить список своих запросов вместе с данными об ответах на них.
Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате: id вещи, название, id владельца.
Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию о каждой вещи.
Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями.
         С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить.
         Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично.
         Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения.
GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате, что и в эндпоинте GET /requests.
         Посмотреть данные об отдельном запросе может любой пользователь.*/