package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void addNewRequestWhenDescriptionIsNull() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        Optional<Long> userId = Optional.of(1L);

        assertThrows(ItemRequestValidationException.class,
                () -> itemRequestService.addNewRequest(userId, itemRequestDto));
    }

    @Test
    void addNewRequestWhenDescriptionIsEmpty() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("");
        Optional<Long> userId = Optional.of(1L);

        assertThrows(ItemRequestValidationException.class,
                () -> itemRequestService.addNewRequest(userId, itemRequestDto));
    }

    @Test
    void addNewRequestWhenUserIdEmpty() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Some description");
        Optional<Long> userId = Optional.empty();

        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.addNewRequest(userId, itemRequestDto));
    }

    @Test
    void addNewRequestTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Some description");
        Optional<Long> userId = Optional.of(1L);

        when(userRepository.findById(userId.get())).thenReturn(Optional.of(new User()));

        itemRequestService.addNewRequest(userId, itemRequestDto);

        verify(itemRequestRepository).save(any());
    }

    @Test
    void getUserRequests() {
        Optional<Long> userId = Optional.of(1L);

        when(userRepository.findById(userId.get())).thenReturn(Optional.of(new User()));

        itemRequestService.getUserRequests(userId);

        verify(itemRequestRepository).findByUserEqualsOrderByCreatedDesc(any());
    }

    @Test
    void getAllRequestsWhenPaginationParametersInvalid() {
        Optional<Long> userId = Optional.empty();
        Integer from = null;
        Integer size = 5;

        List<ItemRequestDto> list = itemRequestService.getAllRequests(from, size, userId);

        assertEquals(0, list.size());
    }

    @Test
    void getAllRequestsWhenPaginationParametersNegative() {
        Optional<Long> userId = Optional.empty();
        Integer from = -5;
        Integer size = 5;

        assertThrows(PagingValidationException.class,
                () -> itemRequestService.getAllRequests(from, size, userId));
    }

    @Test
    void getAllRequestsTest() {
        Integer from = 0;
        Integer size = 5;
        Optional<Long> userId = Optional.of(1L);

        PageRequest pr = PageRequest.of((from / size), size);

        when(userRepository.findById(userId.get())).thenReturn(Optional.of(new User()));

        itemRequestService.getAllRequests(from, size, userId);

        verify(itemRequestRepository).findAllByUserNot(any(), any());
    }

    @Test
    void getRequestTest() {
        Optional<Long> userId = Optional.of(1L);
        long requestId = 1L;

        when(userRepository.findById(userId.get())).thenReturn(Optional.of(new User()));

        assertThrows(ItemNotFoundException.class,
                () -> itemRequestService.getRequest(userId, requestId));
    }
}