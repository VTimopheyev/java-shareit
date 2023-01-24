package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestService {
    ItemRequest addNewRequest(Optional<Long> userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getUserRequests(Optional<Long> userId);

    List<ItemRequestDto> getAllRequests(Integer from, Integer size, Optional<Long> userId);

    ItemRequestDto getRequest(Optional<Long> userId, long requestId);
}
