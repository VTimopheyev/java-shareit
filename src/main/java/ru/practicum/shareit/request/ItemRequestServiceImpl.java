package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequest addNewRequest(Optional<Long> userId, ItemRequestDto itemRequestDto) {
        if (Objects.isNull(itemRequestDto.getDescription()) || itemRequestDto.getDescription().isEmpty()) {
            throw new ItemRequestValidationException();
        }

        User user = validateUser(userId);

        ItemRequest r = new ItemRequest();
        r.setDescription(itemRequestDto.getDescription());
        r.setCreated(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        r.setUser(userRepository.findOneById(userId));

        return itemRequestRepository.save(r);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Optional<Long> userId) {
        User user = validateUser(userId);

        return itemRequestRepository
                .findByUserEqualsOrderByCreatedDesc(user)
                .stream()
                .map(i -> {
                    return itemRequestMapper.toItemRequestDto(i, itemRepository
                            .findByRequestEquals(i)
                            .stream()
                            .map(item -> itemMapper.toItemDto(item, new ArrayList<>()))
                            .collect(Collectors.toList()));
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<ItemRequestDto> getAllRequests(Integer from, Integer size, Optional<Long> userId) {

        if (Objects.isNull(from) || Objects.isNull(size)) {
            return new ArrayList<>();
        }

        if (from < 0 || size <= 0) {
            throw new PagingValidationException();
        }

        User user = validateUser(userId);

        PageRequest pr = PageRequest.of((from / size), size);

        return itemRequestRepository
                .findAllByUserNot(user, pr)
                .stream()
                .map(i -> {
                    return itemRequestMapper.toItemRequestDto(i, itemRepository
                            .findByRequestEquals(i)
                            .stream()
                            .map(item -> itemMapper.toItemDto(item, new ArrayList<>()))
                            .collect(Collectors.toList()));
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequest(Optional<Long> userId, long requestId) {
        User user = validateUser(userId);

        return itemRequestRepository
                .findById(requestId)
                .map(i -> {
                    return itemRequestMapper.toItemRequestDto(i, itemRepository
                            .findByRequestEquals(i)
                            .stream()
                            .map(item -> itemMapper.toItemDto(item, new ArrayList<>()))
                            .collect(Collectors.toList()));
                })
                .orElseThrow(ItemNotFoundException::new);
    }

    private User validateUser(Optional<Long> userId) {
        if (userId.isEmpty()) {
            throw new UserNotFoundException();
        }

        return userRepository.findById(userId.get())
                .orElseThrow(UserNotFoundException::new);
    }
}