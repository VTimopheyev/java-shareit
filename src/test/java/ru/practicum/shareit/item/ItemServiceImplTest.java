package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.PagingValidationException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentsRepository commentsRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;


    @Test
    void addNewItemWhenUserIdEmpty() {

        Optional<Long> userId = Optional.empty();
        ItemDto itemDto = new ItemDto();

        assertThrows(UserValidationException.class,
                () -> itemService.addNewItem(userId, itemDto));
    }

    @Test
    void addNewItemWhenItemRequestIsEmptyEmpty() {

        Optional<Long> userId = Optional.of(1L);
        User user = new User();
        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Name");
        itemDto1.setDescription("Some description");
        itemDto1.setAvailable(true);
        Item item = new Item();
        List<Comment> list = new ArrayList<>();

        when(userRepository.findById(userId.get())).thenReturn(Optional.of(user));
        when(itemMapper.toItem(itemDto1)).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(item);
        when(commentsRepository.findByItemEquals(item)).thenReturn(list);
        when(itemMapper.toItemDto(item, list)).thenReturn(new ItemDto());

        ItemDto result = itemService.addNewItem(userId, itemDto1);

        assertNotNull(result);

    }

    @Test
    void addNewItemWhenItemRequestIsNotEmptyEmpty() {

        Optional<Long> userId = Optional.of(1L);
        User user = new User();
        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Name");
        itemDto1.setDescription("Some description");
        itemDto1.setAvailable(true);
        itemDto1.setRequestId(2L);
        Item item = new Item();
        List<Comment> list = new ArrayList<>();

        when(itemRequestRepository.findById(itemDto1.getRequestId()))
                .thenReturn(Optional.of(new ItemRequest()));
        when(userRepository.findById(userId.get())).thenReturn(Optional.of(user));
        when(itemMapper.toItem(itemDto1)).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(item);
        when(commentsRepository.findByItemEquals(item)).thenReturn(list);
        when(itemMapper.toItemDto(item, list)).thenReturn(new ItemDto());

        ItemDto result = itemService.addNewItem(userId, itemDto1);

        assertNotNull(result);

    }

    @Test
    void getItemForOwner() {
        Item item = new Item();
        BookingDto bookingDto = new BookingDto();
        BookingDto bookingDto1 = new BookingDto();
        List<Comment> comments = new ArrayList<>();

        when(commentsRepository.findByItemEquals(item)).thenReturn(comments);
        when(itemMapper.toOwnerItemDto(item, bookingDto, bookingDto1, comments)).thenReturn(new OwnerItemDto());

        OwnerItemDto result = itemService.getItemForOwner(item, bookingDto, bookingDto1);

        assertNotNull(result);

    }

    @Test
    void addNewCommentWhenCommentIsEmpty() {
        Optional<Long> userId = Optional.of(1L);
        long itemId = 1L;
        Comment comment = new Comment();
        comment.setText("");

        assertThrows(CommentValidationException.class,
                () -> itemService.addNewComment(userId, itemId, comment));

    }

    @Test
    void addNewCommentWhenCommentIsNotEmpty() {
        Optional<Long> userId = Optional.of(1L);
        long itemId = 1L;
        Comment comment = new Comment();
        comment.setText("something here");
        User author = new User();
        author.setName("Bob");
        Item item = new Item();

        when(userRepository.findById(userId.get())).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentsRepository.save(any())).thenReturn(new Comment());

        Comment result = itemService.addNewComment(userId, itemId, comment);

        assertNotNull(result);
    }

    @Test
    void convertToOwnerItemDto() {
        Item item = new Item();
        BookingDto bookingDto1 = new BookingDto();
        BookingDto bookingDto2 = new BookingDto();
        List<Comment> comments = new ArrayList<>();

        when(commentsRepository.findByItemEquals(item)).thenReturn(comments);
        when(itemMapper.toOwnerItemDto(item, bookingDto1, bookingDto2, comments))
                .thenReturn(new OwnerItemDto());

        OwnerItemDto o = itemService.convertToOwnerItemDto(item, bookingDto1, bookingDto2);
        assertNotNull(o);
    }

    @Test
    void getAllItemsWhenPaginationParametersInvalid() {
        User user = new User();
        Integer from = null;
        Integer size = 5;

        List<Item> result = itemService.getAllItems(user, from, size);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllItemsWhenPaginationParametersNegative() {
        User user = new User();
        Integer from = 0;
        Integer size = -5;

        assertThrows(PagingValidationException.class,
                () -> itemService.getAllItems(user, from, size));
    }

    @Test
    void getAllItemsTest() {
        User user = new User();
        Integer from = 0;
        Integer size = 5;

        PageRequest pr = PageRequest.of((from / size), size);

        when(itemRepository.findAllByOwner(user, pr)).thenReturn(new ArrayList<>());

        List<Item> result = itemService.getAllItems(user, from, size);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateItemWhenUserIdIsEmpty() {
        Optional<Long> userId = Optional.empty();
        long itemId = 1L;
        ItemDto itemDto = new ItemDto();

        assertThrows(UserValidationException.class,
                () -> itemService.updateItem(userId, itemId, itemDto));
    }

    @Test
    void updateItemWhenUserNotExists() {
        Optional<Long> userId = Optional.of(1L);
        long itemId = 1L;
        ItemDto itemDto = new ItemDto();

        when(userService.checkUserExists(userId.get())).thenReturn(false);

        assertThrows(UserValidationException.class,
                () -> itemService.updateItem(userId, itemId, itemDto));
    }

    @Test
    void updateItemWhenItemNotExists() {
        Optional<Long> userId = Optional.of(1L);
        long itemId = 1L;
        ItemDto itemDto = new ItemDto();

        when(userService.checkUserExists(userId.get())).thenReturn(true);
        when(itemRepository.findById(userId.get())).thenReturn(Optional.empty());

        assertThrows(UserValidationException.class,
                () -> itemService.updateItem(userId, itemId, itemDto));
    }

    @Test
    void updateItemWhenUserNotOwner() {
        Optional<Long> userId = Optional.of(1L);
        long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        Item item = new Item();
        User user = new User();
        user.setId(2L);
        item.setOwner(user);


        when(userService.checkUserExists(userId.get())).thenReturn(true);
        when(itemRepository.findById(userId.get())).thenReturn(Optional.of(item));

        assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(userId, itemId, itemDto));
    }

    @Test
    void updateItemTest() {
        Optional<Long> userId = Optional.of(1L);
        long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Gun");
        itemDto.setAvailable(true);
        itemDto.setDescription("Good thing");
        Item item = new Item();
        User user = new User();
        user.setId(1L);
        item.setOwner(user);
        List<Comment> comments = new ArrayList<>();


        when(userService.checkUserExists(userId.get())).thenReturn(true);
        when(itemRepository.findById(userId.get())).thenReturn(Optional.of(item));
        when(commentsRepository.findByItemEquals(item)).thenReturn(comments);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item, comments)).thenReturn(new ItemDto());

        ItemDto result = itemService.updateItem(userId, itemId, itemDto);

        assertNotNull(result);
    }

    @Test
    void deleteItemWhenUserIdIsEmpty() {
        Optional<Long> userId = Optional.empty();
        long itemId = 1L;

        assertThrows(UserValidationException.class,
                () -> itemService.deleteItem(userId, itemId));
    }

    @Test
    void deleteItemWhenUserNotExists() {
        Optional<Long> userId = Optional.of(1L);
        long itemId = 1L;

        when(userService.checkUserExists(userId.get())).thenReturn(false);

        assertThrows(UserValidationException.class,
                () -> itemService.deleteItem(userId, itemId));
    }

    @Test
    void deleteItemWhenItemNotExists() {
        Optional<Long> userId = Optional.of(1L);
        long itemId = 1L;

        when(userService.checkUserExists(userId.get())).thenReturn(true);
        when(itemRepository.findById(userId.get())).thenReturn(Optional.empty());

        assertThrows(UserValidationException.class,
                () -> itemService.deleteItem(userId, itemId));
    }

    @Test
    void deleteItemWhenUserNotOwner() {
        Optional<Long> userId = Optional.of(1L);
        long itemId = 1L;
        Item item = new Item();
        User user = new User();
        user.setId(2L);
        item.setOwner(user);

        when(userService.checkUserExists(userId.get())).thenReturn(true);
        when(itemRepository.findById(userId.get())).thenReturn(Optional.of(item));

        assertThrows(ItemNotFoundException.class,
                () -> itemService.deleteItem(userId, itemId));
    }

    @Test
    void deleteItemTest() {
        Optional<Long> userId = Optional.of(1L);
        long itemId = 1L;
        Item item = new Item();
        User user = new User();
        user.setId(1L);
        item.setOwner(user);

        when(userService.checkUserExists(userId.get())).thenReturn(true);
        when(itemRepository.findById(userId.get())).thenReturn(Optional.of(item));

        itemService.deleteItem(userId, itemId);

        verify(itemRepository).deleteById(any());
    }


    @Test
    void searchItemsWhenUserIdIsEmpty() {
        Optional<Long> userId = Optional.empty();
        String text = "Some text";
        Integer from = 0;
        Integer size = 5;

        assertThrows(UserValidationException.class,
                () -> itemService.searchItems(userId, text, from, size));

    }

    @Test
    void searchItemsWhenUserNotExists() {
        Optional<Long> userId = Optional.of(1L);
        String text = "Some text";
        Integer from = 0;
        Integer size = 5;

        when(userService.checkUserExists(userId.get())).thenReturn(false);

        assertThrows(UserValidationException.class,
                () -> itemService.searchItems(userId, text, from, size));

    }

    @Test
    void searchItemsWhenTextIsEmpty() {
        Optional<Long> userId = Optional.of(1L);
        String text = "";
        Integer from = 0;
        Integer size = 5;

        when(userService.checkUserExists(userId.get())).thenReturn(true);

        List<ItemDto> list = itemService.searchItems(userId, text, from, size);

        assertTrue(list.isEmpty());
    }

    @Test
    void searchItemsWhenPaginationInvalid() {
        Optional<Long> userId = Optional.of(1L);
        String text = "Something";
        Integer from = null;
        Integer size = 5;

        when(userService.checkUserExists(userId.get())).thenReturn(true);

        List<ItemDto> list = itemService.searchItems(userId, text, from, size);

        assertTrue(list.isEmpty());
    }

    @Test
    void searchItemsWhenPaginationNegative() {
        Optional<Long> userId = Optional.of(1L);
        String text = "Something";
        Integer from = -5;
        Integer size = 5;

        when(userService.checkUserExists(userId.get())).thenReturn(true);

        assertThrows(PagingValidationException.class,
                () -> itemService.searchItems(userId, text, from, size));
    }

    @Test
    void searchItemsTest() {
        Optional<Long> userId = Optional.of(1L);
        String text = "Something";
        Integer from = 0;
        Integer size = 5;
        PageRequest pr = PageRequest.of((from / size), size);

        when(userService.checkUserExists(userId.get())).thenReturn(true);

        itemService.searchItems(userId, text, from, size);

        verify(itemRepository).findByNameContainingOrDescriptionContainingIgnoreCase(
                text, text, pr);
    }

    @Test
    void getItemById() {
        long id = 1L;

        when(itemRepository.findById(id)).thenReturn(Optional.of(new Item()));
        itemService.getItemById(id);

        verify(itemRepository).findById(id);

    }
}